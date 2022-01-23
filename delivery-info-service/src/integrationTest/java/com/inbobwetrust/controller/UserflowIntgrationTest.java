package com.inbobwetrust.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("production")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserflowIntgrationTest {
  private static final String DEFAULT_MONGO_DATABASE = "inbob";
  @Autowired WebTestClient testClient;

  @Autowired DeliveryRepository deliveryRepository;

  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  private String proxyShopUrl = "/relay/v1/shop";

  private String proxyAgencyUrl = "/relay/v1/agency";

  final Delivery baseDelivery = makeDelivery();

  @BeforeAll
  void beforeAll() throws JsonProcessingException {
    stubFor(
        post(urlPathMatching(proxyShopUrl + "/" + baseDelivery.getShopId()))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(baseDelivery))));
  }

  @Test
  @Order(1)
  @DisplayName("전체 유저플로우 테스트 [신규주문]")
  void userFlow() throws JsonProcessingException {
    // given
    var newDelivery = baseDelivery.deepCopy();

    // Act
    newDelivery.setDeliveryStatus(DeliveryStatus.NEW);
    testClient
        .post()
        .uri("/api/delivery")
        .bodyValue(newDelivery)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Delivery.class)
        .consumeWith(
            res -> {
              var stream = deliveryRepository.findById(newDelivery.getId());
              StepVerifier.create(stream)
                  .expectNextMatches(del -> del.getDeliveryStatus().equals(DeliveryStatus.NEW));
            });
  }

  @Test
  @Order(2)
  @DisplayName("전체 유저플로우 테스트 [주문접수]")
  void acceptDeliveryTest() throws JsonProcessingException {
    // given
    var newDelivery = baseDelivery.deepCopy();
    stubFor(
        post(urlPathMatching(proxyAgencyUrl + "/.*"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(baseDelivery))));
    // Act
    newDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    testClient
        .put()
        .uri("/api/delivery/accept")
        .bodyValue(newDelivery)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Delivery.class)
        .consumeWith(
            res -> {
              var stream = deliveryRepository.findById(newDelivery.getId());
              StepVerifier.create(stream)
                  .expectNextMatches(
                      del -> del.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED));
            });
  }

  @Test
  @Order(3)
  @DisplayName("전체 유저플로우 테스트 [라이더배정]")
  void setDeliveryRiderTest() throws JsonProcessingException {

    // given
    var newDelivery = baseDelivery.deepCopy();
    // Act
    newDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    testClient
        .put()
        .uri("/api/delivery/rider")
        .bodyValue(newDelivery)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Delivery.class)
        .consumeWith(
            res -> {
              var stream = deliveryRepository.findById(newDelivery.getId());
              StepVerifier.create(stream)
                  .expectNextMatches(
                      del -> del.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED));
            });
  }

  static Delivery makeDelivery() {
    return Delivery.builder()
        .id(LocalDateTime.now().toString())
        .orderId(LocalDateTime.now().toString())
        .customerId("customer-1234")
        .shopId("shop-1234")
        .agencyId("agency-124")
        .address("서울시 강남구 삼성동 봉은사로 12-41")
        .deliveryStatus(null)
        .phoneNumber("01031583212")
        .orderTime(LocalDateTime.now())
        .pickupTime(LocalDateTime.now().plusMinutes(1))
        .finishTime(LocalDateTime.now().plusMinutes(2))
        .build();
  }
}
