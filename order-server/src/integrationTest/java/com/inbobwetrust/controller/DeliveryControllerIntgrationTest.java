package com.inbobwetrust.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.exception.RelayClientException;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
class DeliveryControllerIntegrationTest {
  @Autowired WebTestClient testClient;

  @Autowired DeliveryRepository deliveryRepository;
  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @BeforeEach
  void setUp() {
    var setUpDatabase = deliveryRepository.deleteAll();
    StepVerifier.create(setUpDatabase).expectNext().verifyComplete();
  }

  private Delivery makeValidDelivery() {
    return Delivery.builder()
        .shopId("shop1234")
        .orderId("order-1234")
        .customerId("customer-1234")
        .address("서울시 강남구 삼성동 봉은사로 12-41")
        .phoneNumber("01031583212")
        .orderTime(LocalDateTime.now())
        .build();
  }

  @Test
  void sendAddDeliveryEvent_success() throws JsonProcessingException {
    // Arrange
    Delivery expected = makeValidDelivery();
    final String testUrl = "/shop/" + expected.getShopId();
    // Stub
    stubFor(
        post(urlEqualTo(testUrl))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(expected))));
    // Act
    var actual =
        testClient
            .post()
            .uri("/api/delivery")
            .bodyValue(expected)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();
    expected.setId(Objects.requireNonNull(actual).getId());
    var savedCnt = deliveryRepository.findAll();
    // Assert
    WireMock.verify(1, postRequestedFor(urlEqualTo(testUrl)));
    StepVerifier.create(savedCnt).expectNextCount(1).verifyComplete();
  }

  @Test
  void sendAddDeliveryEvent_connection_refused() {
    // Arrange
    Delivery expected = makeValidDelivery();
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
  void sendAddDeliveryEvent_server_error() {
    // Arrange
    Delivery delivery = makeValidDelivery();
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
    var actual =
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
    var saved = deliveryRepository.findAll().blockFirst();
    // Assert
    var expected =
        "Shop operation failed for delivery :     "
            .concat(Objects.requireNonNull(saved).toString());
    Assertions.assertEquals(expected, actual);
    WireMock.verify(1, postRequestedFor(urlEqualTo(testUrl)));
  }
}
