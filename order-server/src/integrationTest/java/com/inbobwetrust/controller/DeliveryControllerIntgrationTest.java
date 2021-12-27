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
import org.junit.jupiter.api.Test;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.inbobwetrust.controller.TestParameterGenerator.generate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
public class DeliveryControllerIntgrationTest {
  @Autowired WebTestClient testClient;

  @Autowired DeliveryRepository deliveryRepository;
  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  static List<Delivery> possibleDeliveries = generate();

  private final Random random = new Random();

  @BeforeEach
  void setUp() {
    var setUpDatabase = deliveryRepository.deleteAll();
    StepVerifier.create(setUpDatabase).expectNext().verifyComplete();
  }

  @Test
  @DisplayName("[신규주문수신 : 성공]")
  void sendAddDeliveryEvent() throws JsonProcessingException {
    for (Delivery delivery : Collections.unmodifiableList(possibleDeliveries)) {
      sendAddDeliveryEvent_success(delivery);
    }
  }

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
    var actual =
        testClient
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

  @Test
  @DisplayName("[서버-신규주문수신 : 실패] : 사장님 PC앱 네트워크 요청 실패")
  void sendAddDeliveryEvent_fail() {
    for (Delivery delivery : Collections.unmodifiableList(possibleDeliveries)) {
      sendAddDeliveryEvent_connection_refused(delivery);
    }
  }

  void sendAddDeliveryEvent_connection_refused(final Delivery expected) {
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
    var actual =
        testClient
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

  @Test
  @DisplayName("[서버-신규주문수신 : 실패] 사장님 PC앱 에러")
  void sendAddDeliveryEvent_server_error() {
    for (Delivery delivery : Collections.unmodifiableList(possibleDeliveries)) {
      sendAddDeliveryEvent_server_error(delivery);
    }
  }

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
    var errorMsg =
        testClient
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
    assert errorMsg != null;
    Assertions.assertTrue(errorMsg.contains("Shop operation failed for delivery :     "));
    WireMock.verify(1, postRequestedFor(urlEqualTo(testUrl)));
  }

  @Test
  @DisplayName("[서버-신규주문수신 : 실패] 사장님 PC앱 에러")
  void acceptDelivery() throws JsonProcessingException {
    for (Delivery delivery : Collections.unmodifiableList(possibleDeliveries)) {
      acceptDelivery(delivery);
    }
  }

  void acceptDelivery(Delivery delivery) throws JsonProcessingException {
    // Arrange
    delivery.setDeliveryStatus(DeliveryStatus.NEW);
    var saved = deliveryRepository.save(delivery).block();
    assert saved != null;
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
      testClient
          .put()
          .uri("/api/delivery/accept")
          .bodyValue(expected)
          .exchange()
          .expectStatus()
          .isEqualTo(HttpStatus.BAD_REQUEST)
          .expectBody(String.class);
    } else {
      var responseBody =
          testClient
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
  }

  @Test
  @DisplayName("[배달대행사-라이더배정: 성공]")
  void setDeliveryRider() {
    for (Delivery delivery : Collections.unmodifiableList(possibleDeliveries)) {
      setDeliveryRider(delivery);
    }
  }

  void setDeliveryRider(Delivery delivery) {
    delivery.setRiderId(null);
    var expected = deliveryRepository.save(delivery).block();
    assert expected != null;
    // Act
    if (expected.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED)) {
      var actual =
          testClient
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
      assert actual != null;
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
          .expectBody(String.class);
    }
  }

  @Test
  @DisplayName("[주문픽업 이벤트 : 성공]")
  void setPickedUp() {
    for (Delivery delivery : Collections.unmodifiableList(possibleDeliveries)) {
      setPickedUp(delivery);
    }
  }

  void setPickedUp(Delivery delivery) {
    // Arrange
    var before = deliveryRepository.save(delivery).block();
    assert before != null;
    var expected = before.deepCopy();
    expected.setDeliveryStatus(before.getDeliveryStatus().getNext());
    //
    if (before.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED)) {
      // Act
      var actual =
          testClient
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
      assert actual != null;
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

  @Test
  @DisplayName("[배달완료 이벤트 : 성공]")
  void setComplete() {
    for (Delivery delivery : Collections.unmodifiableList(possibleDeliveries)) {
      setComplete(delivery);
    }
  }

  void setComplete(Delivery delivery) {
    // Arrange
    var expected = deliveryRepository.save(delivery).block();
    assert expected != null;
    if (expected.getDeliveryStatus().equals(DeliveryStatus.PICKED_UP)) {
      // Act
      var actual =
          testClient
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
      assert actual != null;
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

  @Test
  @DisplayName("[주문조회 전체 w/페이징 : 성공]")
  void getDeliveries() {
    var deliveries = Collections.unmodifiableList(possibleDeliveries);
    // Arrange
    var savedStream = deliveryRepository.saveAll(deliveries);
    StepVerifier.create(savedStream).expectNextCount(deliveries.size()).verifyComplete();
    int expectedSize = 10;
    int page = 3;
    var uri =
        UriComponentsBuilder.fromUriString("/api/delivery")
            .queryParam("page", page)
            .queryParam("size", expectedSize)
            .buildAndExpand()
            .toUri();
    // Act
    var actual =
        testClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Delivery.class)
            .returnResult()
            .getResponseBody();
    // Assert
    assert actual != null;
    Assertions.assertEquals(expectedSize, actual.size());
  }

  @Test
  @DisplayName("[주문조회 by아이디 : 성공]")
  void getDelivery() {
    for (Delivery delivery : Collections.unmodifiableList(possibleDeliveries)) {
      getDelivery(delivery);
    }
  }

  void getDelivery(Delivery delivery) {
    // Arrange
    var expected = deliveryRepository.save(delivery).block();
    boolean isIdWrong = random.nextBoolean();
    assert expected != null;
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
      var actual =
          testClient
              .get()
              .uri("/api/delivery/{id}", delivery.getId())
              .exchange()
              .expectStatus()
              .isOk()
              .expectBody(Delivery.class)
              .returnResult()
              .getResponseBody();
      // Assert
      assert actual != null;
      expected.copyTimeFields(actual);
      Assertions.assertEquals(expected, actual);
    }
  }
}
