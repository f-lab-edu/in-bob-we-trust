package com.inbobwetrust.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.RelayClientException;
import com.inbobwetrust.repository.primary.DeliveryRepository;
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

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.inbobwetrust.controller.TestParameterGenerator.generate;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test", inheritProfiles = false)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
public class DeliveryControllerIntgrationTest {
  @Autowired WebTestClient testClient;

  @Autowired DeliveryRepository deliveryRepository;

  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  private String proxyShopUrl = "/relay/v1/shop";

  private String proxyAgencyUrl = "/relay/v1/agency";

  static List<Delivery> deliveryList;

  @BeforeEach
  void setUp() {
    var setUpDatabase = deliveryRepository.deleteAll();
    StepVerifier.create(setUpDatabase).expectNext().verifyComplete();
    deliveryList = generate();
  }

  @Test
  @Test
  @DisplayName("[서버-신규주문수신]")
  void sendAddDeliveryEvent_success() throws JsonProcessingException {
    assertTrue(deliveryList.size() > 0);
    // Multiple Tests
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var delivery = deliveryList.get(idx);
      delivery.setShopId(delivery.getShopId() + idx);
      final String testUrl = proxyShopUrl + "/" + delivery.getShopId();

      // Stub
      stubFor(
          post(urlPathEqualTo(testUrl))
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

      // Assert
      WireMock.verify(1, postRequestedFor(urlPathEqualTo(testUrl)));
      delivery.setId(Objects.requireNonNull(actual).getId());
      Assertions.assertEquals(delivery, actual);
    }
  }

  @Test
  @DisplayName("[서버-신규주문수신]")
  void sendAddDeliveryEvent_connection_refused(Delivery expected) {
    assertTrue(deliveryList.size() > 0);
    // Multiple Tests
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      final String testUrl = proxyShopUrl + "/" + expected.getShopId();
      // Stub
      stubFor(
          post(urlPathEqualTo(testUrl))
              .willReturn(
                  aResponse()
                      .withStatus(HttpStatus.NOT_FOUND.value())
                      .withBody(
                          new RelayClientException(
                                  "Push Event failed for delivery :     " + expected)
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
      assertTrue(Objects.requireNonNull(actual).contains("Push Event failed for delivery :     "));
      WireMock.verify(1, postRequestedFor(urlPathEqualTo(testUrl)));
      StepVerifier.create(saved).expectNextCount(1).verifyComplete();
    }
  }

  @Test
  @DisplayName("[서버-신규주문수신 : 서버에러]")
  void sendAddDeliveryEvent_server_error() {
    assertTrue(deliveryList.size() > 0);
    // Multiple Tests
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var delivery = deliveryList.get(idx);
      final String testUrl = proxyShopUrl + "/" + delivery.getShopId();
      // Stub
      stubFor(
          post(urlPathEqualTo(testUrl))
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
      assertTrue(errorMsg.contains("Shop operation failed for delivery :     "));
      WireMock.verify(1, postRequestedFor(urlPathEqualTo(testUrl)));
    }
  }

  @Test
  @DisplayName("[사장님-주문접수 : 성공]")
  void acceptDelivery() throws JsonProcessingException {
    assertTrue(deliveryList.size() > 0);
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      var delivery = deliveryList.get(idx);
      // Arrange
      delivery.setDeliveryStatus(DeliveryStatus.NEW);
      var saved = deliveryRepository.save(delivery).block();
      var expected = saved.deepCopy();
      expected.setDeliveryStatus(DeliveryStatus.ACCEPTED);
      expected.setPickupTime(expected.getPickupTime().plusMinutes(1));
      expected.setFinishTime(expected.getOrderTime().plusMinutes(2));
      final String testUrl = proxyAgencyUrl + "/" + delivery.getAgencyId();
      // Stub
      stubFor(
          post(urlPathEqualTo(testUrl))
              .willReturn(
                  aResponse()
                      .withStatus(HttpStatus.OK.value())
                      .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                      .withBody(mapper.writeValueAsString(expected))));
      // Act
      if (expected.getOrderTime().isAfter(expected.getPickupTime())) {
        var responseBody =
            testClient
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
      WireMock.verify(1, postRequestedFor(urlPathEqualTo(testUrl)));
    }
  }

  @Test
  @DisplayName("[배달대행사-라이더배정: 성공]")
  void setDeliveryRider() {
    assertTrue(deliveryList.size() > 0);
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      var delivery = deliveryList.get(idx);
      delivery.setRiderId(null);
      var expected = deliveryRepository.save(delivery).block();
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
  }

  @Test
  @DisplayName("[주문픽업 이벤트 : 성공]")
  void setPickedUp(Delivery delivery) {
    assertTrue(deliveryList.size() > 0);
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var before = deliveryRepository.save(delivery).block();
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
  }

  @Test
  @DisplayName("[배달완료 이벤트 : 성공]")
  void setComplete(Delivery delivery) {
    assertTrue(deliveryList.size() > 0);
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var expected = deliveryRepository.save(delivery).block();

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
  }

  @Test
  @DisplayName("[주문조회 전체 w/ 페이징]")
  void getDeliveries() {

    assertTrue(deliveryList.size() > 0);
    // Arrange
    var savedStream = deliveryRepository.saveAll(deliveryList);
    StepVerifier.create(savedStream).expectNextCount(deliveryList.size()).verifyComplete();
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
    Assertions.assertEquals(expectedSize, actual.size());
  }

  private final Random random = new Random();

  @Test
  @DisplayName("[주문조회 : by 아이디]")
  void getDelivery() {
    assertTrue(deliveryList.size() > 0);
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      var delivery = deliveryList.get(idx);
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
        expected.copyTimeFields(actual);
        Assertions.assertEquals(expected, actual);
      }
    }
  }
}
