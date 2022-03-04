package com.inbobwetrust.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbobwetrust.repository.DeliveryRepository;
import com.inbobwetrust.repository.RiderLocationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@Testcontainers
public class RiderLocationWriteTest {
  @Autowired WebTestClient testClient;

  @SpyBean
  RiderLocationRepository locationRepository;

  @SpyBean RiderLocationService locationService;

  @SpyBean
  DeliveryRepository deliveryRepository;

  @Container static GenericContainer<?> redisContainer = startRedisContainer();

  private static final int REDIS_DEFAULT_PORT = 6379;

  private final ObjectMapper mapper = new ObjectMapper();

  private static GenericContainer<?> startRedisContainer() {
    return new GenericContainer<>("redis:latest").withExposedPorts(REDIS_DEFAULT_PORT);
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    if (!redisContainer.isRunning()) {
      redisContainer.start();
    }
    var redisContainerPort = redisContainer.getMappedPort(REDIS_DEFAULT_PORT);
    registry.add("spring.redis.port", () -> Objects.requireNonNull(redisContainerPort));
  }

  @BeforeEach
  void setup() throws JsonProcessingException {
    locationRepository.deleteAll().blockLast();
    var caches = locationRepository.findAll().collectList().block();
    assertEquals(0, caches.size());
  }

  @Test
  @DisplayName("Cache에 없고 픽업완료상태가 아니므로 false를 리턴해야한다.")
  void updateLocation_test() throws JsonProcessingException {
    // given
    var riderLocation =
        new RiderLocation(LocalDateTime.now().toString(), "delivery1234zz-false", 23.0f, 190f);
    stubFor(
        get(urlMatching("/is-picked-up/delivery1234zz-false"))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(Boolean.FALSE))
                    .withStatus(HttpStatus.OK.value())));
    // when
    testClient
        .put()
        .uri("/rider/location")
        .bodyValue(riderLocation)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Boolean.class)
        .isEqualTo(false);
    // then
    verify(locationService, times(1)).tryPutOperation(any(RiderLocation.class));
    verify(locationRepository, times(1)).setIfPresent(riderLocation);
    verify(deliveryRepository, times(1)).isPickedUp(riderLocation.getDeliveryId());
  }

  @Test
  @DisplayName("Cache에는 없지만 픽업완료 상태라면 True를 리턴하고 Cache에 값이 있어야한다.")
  void updateLocation_test_success() throws JsonProcessingException {
    // given
    StepVerifier.create(locationRepository.findAll()).expectNextCount(0).verifyComplete();
    var riderLocation = new RiderLocation("rider-1234", "delivery1234zz-true", 23.0f, 190f);
    stubFor(
        get(urlMatching("/is-picked-up/" + riderLocation.getDeliveryId()))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(Boolean.TRUE))
                    .withStatus(HttpStatus.OK.value())));
    // when
    testClient
        .put()
        .uri("/rider/location")
        .bodyValue(riderLocation)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Boolean.class)
        .isEqualTo(Boolean.TRUE);
    // then
    var cache = locationRepository.findAll().collectList().block();
    System.out.println(cache);
    verify(locationService, times(1)).tryPutOperation(any(RiderLocation.class));
    verify(locationRepository, times(1)).setIfPresent(riderLocation);
    verify(deliveryRepository, times(1)).isPickedUp(riderLocation.getDeliveryId());
  }

  @Test
  @DisplayName("Cache에 있다면 True를 리턴하고 Cache에 값이 있어야한다.")
  void updateLocation_test_success2() throws JsonProcessingException {
    // given
    var deliveryId = LocalDateTime.now().toString();
    var riderLocation = new RiderLocation(deliveryId, deliveryId, 23.0f, 190f);
    var isSaved = locationRepository.setIfAbsent(riderLocation).block();
    assertTrue(isSaved);

    // when
    testClient
        .put()
        .uri("/rider/location")
        .bodyValue(riderLocation)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Boolean.class)
        .isEqualTo(Boolean.TRUE);

    // then
    StepVerifier.create(locationRepository.findAll()).expectNext(riderLocation).verifyComplete();
    verify(locationService, times(1)).tryPutOperation(any(RiderLocation.class));
    verify(locationRepository, times(1)).setIfPresent(riderLocation);
    verify(deliveryRepository, times(0)).isPickedUp(any());
  }

  @Test
  @DisplayName("라이더 위치 조회 테스트")
  void getLocationTest() {
    // given
    var deliveryId = LocalDateTime.now().toString();
    var riderLocation = new RiderLocation(deliveryId, deliveryId, 180.0f, 180f);
    var saved = locationRepository.setIfAbsent(riderLocation).block();
    assertEquals(true, saved);
    var uri =
        UriComponentsBuilder.fromUriString("/rider/location")
            .queryParam("deliveryId", riderLocation.getDeliveryId())
            .buildAndExpand()
            .toUri();
    // when
    var getLocationResult =
        testClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(RiderLocation.class)
            .returnResult()
            .getResponseBody();
    // then
    Assertions.assertEquals(riderLocation, getLocationResult);
  }
}
