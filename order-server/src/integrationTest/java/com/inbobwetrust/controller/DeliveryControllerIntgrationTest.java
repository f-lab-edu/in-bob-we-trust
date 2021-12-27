package com.inbobwetrust.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.RelayClientException;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.inbobwetrust.controller.TestParameterGenerator.generate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
public class DeliveryControllerIntgrationTest {
  @Autowired
  WebTestClient testClient;

  @Autowired
  DeliveryRepository deliveryRepository;
  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  static List<Delivery> possibleDeliveries = Collections.unmodifiableList(generate());

  static Stream<Arguments> possibleDeliveryStream() {
    return possibleDeliveries.stream().map(Arguments::of).unordered();
  }

  static Stream<Arguments> possibleAllDelivery() {
    return Stream.of(Arguments.arguments(possibleDeliveries));
  }

  @BeforeEach
  void setUp() {
    var setUpDatabase = deliveryRepository.deleteAll();
    StepVerifier.create(setUpDatabase).expectNext().verifyComplete();
  }

  @DisplayName("[서버-신규주문수신]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleDeliveryStream")
  void sendAddDeliveryEvent_success(Delivery delivery) throws JsonProcessingException {
    // Arrange
    final String testUrl = "/shop/" + delivery.getShopId();
    // Stub
    stubFor(
        post(urlEqualTo(testUrl))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(delivery))));
    // Act
    var actual = testClient
        .post()
        .uri("/api/delivery")
        .bodyValue(delivery)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Delivery.class)
        .returnResult()
        .getResponseBody();
    delivery.setId(Objects.requireNonNull(actual).getId());
    var savedCnt = deliveryRepository.findAll();
    // Assert
    WireMock.verify(1, postRequestedFor(urlEqualTo(testUrl)));
    StepVerifier.create(savedCnt).expectNextCount(1).verifyComplete();
  }

  @DisplayName("[서버-신규주문수신]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleDeliveryStream")
  void sendAddDeliveryEvent_connection_refused(Delivery expected) {
    // Arrange
    final String testUrl = "/shop/" + expected.getShopId();
    // Stub
    stubFor(
        post(urlEqualTo(testUrl))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value())
                    .withBody(
                        new RelayClientException("Push Event failed for delivery :     " + expected)
                            .getMessage())));
    // Act
    var actual = testClient
        .post()
        .uri("/api/delivery")
        .bodyValue(expected)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody();
    var saved = deliveryRepository.findAll();
    // Assert
    Assertions.assertTrue(
        Objects.requireNonNull(actual).contains("Push Event failed for delivery :     "));
    WireMock.verify(1, postRequestedFor(urlEqualTo(testUrl)));
    StepVerifier.create(saved).expectNextCount(1).verifyComplete();
  }

  @DisplayName("[서버-신규주문수신 : 서버에러]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleDeliveryStream")
  void sendAddDeliveryEvent_server_error(Delivery delivery) {
    // Arrange
    final String testUrl = "/shop/" + delivery.getShopId();
    // Stub
    stubFor(
        post(urlEqualTo(testUrl))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .withBody(
                        new RelayClientException(
                            "Shop operation failed for delivery :     " + delivery)
                                .getMessage())));
    // Act
    var errorMsg = testClient
        .post()
        .uri("/api/delivery")
        .bodyValue(delivery)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody();
    // Assert
    Assertions.assertTrue(errorMsg.contains("Shop operation failed for delivery :     "));
    WireMock.verify(1, postRequestedFor(urlEqualTo(testUrl)));
  }

  @DisplayName("[사장님-주문접수 : 성공]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleDeliveryStream")
  void acceptDelivery(Delivery delivery) throws JsonProcessingException {
    // Arrange
    delivery.setDeliveryStatus(DeliveryStatus.NEW);
    var saved = deliveryRepository.save(delivery).block();
    var expected = saved.deepCopy();
    expected.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    expected.setPickupTime(expected.getPickupTime().plusMinutes(1));
    expected.setFinishTime(expected.getOrderTime().plusMinutes(2));
    final String testUrl = "/agency/" + delivery.getAgencyId();
    // Stub
    stubFor(
        post(urlPathMatching(testUrl))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(expected))));
    // Act
    if (expected.getOrderTime().isAfter(expected.getPickupTime())) {
      var responseBody = testClient
          .put()
          .uri("/api/delivery/accept")
          .bodyValue(expected)
          .exchange()
          .expectStatus()
          .isEqualTo(HttpStatus.BAD_REQUEST)
          .expectBody(String.class)
          .returnResult();
      return;
    }
    var responseBody = testClient
        .put()
        .uri("/api/delivery/accept")
        .bodyValue(expected)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Delivery.class)
        .returnResult()
        .getResponseBody();
    // Assert
    Assertions.assertEquals(expected, responseBody);
    WireMock.verify(1, postRequestedFor(urlEqualTo(testUrl)));
  }

  @DisplayName("[배달대행사-라이더배정: 성공]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleDeliveryStream")
  void setDeliveryRider(Delivery delivery) {
    delivery.setRiderId(null);
    var expected = deliveryRepository.save(delivery).block();
    // Act
    if (expected.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED)) {
      var actual = testClient
          .put()
          .uri("/api/delivery/rider")
          .bodyValue(expected)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(Delivery.class)
          .returnResult()
          .getResponseBody();
      // Assert
      expected.copyTimeFields(actual);
      Assertions.assertEquals(expected, actual);
    } else {
      // Act & Assert
      testClient
          .put()
          .uri("/api/delivery/rider")
          .bodyValue(expected)
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectBody(String.class)
          .returnResult()
          .getResponseBody();
    }
  }

  @DisplayName("[주문픽업 이벤트 : 성공]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleDeliveryStream")
  void setPickedUp(Delivery delivery) {
    // Arrange
    var before = deliveryRepository.save(delivery).block();
    var expected = before.deepCopy();
    expected.setDeliveryStatus(before.getDeliveryStatus().getNext());
    //
    if (before.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED)) {
      // Act
      var actual = testClient
          .put()
          .uri("/api/delivery/pickup")
          .bodyValue(expected)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(Delivery.class)
          .returnResult()
          .getResponseBody();
      // Assert
      expected.copyTimeFields(actual);
      Assertions.assertEquals(expected, actual);
    } else {
      // Act
      testClient
          .put()
          .uri("/api/delivery/pickup")
          .bodyValue(expected)
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectBody(String.class);
    }
  }

  @DisplayName("[배달완료 이벤트 : 성공]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleDeliveryStream")
  void setComplete(Delivery delivery) {
    // Arrange
    var expected = deliveryRepository.save(delivery).block();

    if (expected.getDeliveryStatus().equals(DeliveryStatus.PICKED_UP)) {
      // Act
      var actual = testClient
          .put()
          .uri("/api/delivery/pickup")
          .bodyValue(expected)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(Delivery.class)
          .returnResult()
          .getResponseBody();
      // Assert
      expected.copyTimeFields(actual);
      Assertions.assertEquals(expected, actual);
    } else {
      // Act
      testClient
          .put()
          .uri("/api/delivery/pickup")
          .bodyValue(expected)
          .exchange()
          .expectStatus()
          .isBadRequest()
          .expectBody(String.class);
    }
  }

  @DisplayName("[주문조회 전체 w/ 페이징]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleAllDelivery")
  void getDeliveries(List<Delivery> deliveries) {
    // Arrange
    var savedStream = deliveryRepository.saveAll(deliveries);
    StepVerifier.create(savedStream).expectNextCount(deliveries.size()).verifyComplete();
    int expectedSize = 10;
    int page = 3;
    var uri = UriComponentsBuilder.fromUriString("/api/delivery")
        .queryParam("page", page)
        .queryParam("size", expectedSize)
        .buildAndExpand()
        .toUri();
    // Act
    var actual = testClient
        .get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Delivery.class)
        .returnResult()
        .getResponseBody();
    // Assert
    Assertions.assertEquals(expectedSize, actual.size());
  }

  private final Random random = new Random();

  @DisplayName("[주문조회 : by 아이디]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("possibleDeliveryStream")
  void getDelivery(Delivery delivery) {
    // Arrange
    var expected = deliveryRepository.save(delivery).block();
    boolean isIdWrong = random.nextBoolean();
    expected.setId(isIdWrong ? expected.getId() + "123" : expected.getId());
    // Act
    if (isIdWrong) {
      testClient
          .get()
          .uri("/api/delivery/{id}", delivery.getId())
          .exchange()
          .expectStatus()
          .isEqualTo(HttpStatus.BAD_REQUEST);
    } else {
      var actual = testClient
          .get()
          .uri("/api/delivery/{id}", delivery.getId())
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(Delivery.class)
          .returnResult()
          .getResponseBody();
      // Assert
      expected.copyTimeFields(actual);
      Assertions.assertEquals(expected, actual);
    }
  }
}
