package com.inbobwetrust.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.RelayClientException;
import com.inbobwetrust.repository.primary.DeliveryRepository;
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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.inbobwetrust.controller.TestParameterGenerator.generate;
import static com.inbobwetrust.domain.DeliveryStatus.ACCEPTED;
import static com.inbobwetrust.domain.DeliveryStatus.PICKED_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    deliveryList = generate();
  }

  @AfterEach
  void tearDown() {}

  @Test
  @DisplayName("[서버-신규주문수신]")
  void sendAddDeliveryEvent_success() throws JsonProcessingException {
    var stubbedDeliery = deliveryList.get(0);
    // Stub for ALl
    var expectedPath = proxyShopUrl + "/.*";
    stubFor(
        post(urlPathMatching(expectedPath))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(stubbedDeliery))));
    // Multiple Tests
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var delivery = deliveryList.get(idx);
      delivery.setShopId(delivery.getShopId());
      var testUrl = proxyShopUrl + "/" + delivery.getShopId();
      var verifyCount = idx + 1;
      // Act
      testClient
          .post()
          .uri("/api/delivery")
          .bodyValue(delivery)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(Delivery.class)
          .consumeWith(
              r -> {
                var deliveryRes = r.getResponseBody();
                assertTrue(Objects.nonNull(deliveryRes));
                assertEquals(delivery.getShopId(), deliveryRes.getShopId());
              });
      // Assert
    }
  }

  @Test
  @DisplayName("[서버-신규주문수신]")
  void sendAddDeliveryEvent_connection_refused() {
    // Stub for All
    var expectedPath = proxyShopUrl + "/.*";
    stubFor(
        post(urlPathEqualTo(expectedPath))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value())
                    .withBody(
                        new RelayClientException("Push Event failed for delivery :")
                            .getMessage())));
    // Multiple Tests
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var expected = deliveryList.get(idx);
      final String testUrl = proxyShopUrl + "/" + expected.getShopId();
      // Stub
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
      // Assert
      assertTrue(Objects.requireNonNull(actual).contains("Push Event failed for delivery :     "));
    }
  }

  @Test
  @DisplayName("[서버-신규주문수신 : 서버에러]")
  void sendAddDeliveryEvent_server_error() {
    // Multiple Tests
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var delivery = deliveryList.get(idx);
      final String testUrl = proxyShopUrl + "/" + delivery.getShopId();
      var expectedCount = idx + 1;
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
      testClient
          .post()
          .uri("/api/delivery")
          .bodyValue(delivery)
          .exchange()
          .expectStatus()
          .is4xxClientError()
          .expectBody(String.class)
          .consumeWith(
              resBody -> {
                var errorMsg = Objects.requireNonNull(resBody.getResponseBody());
                assertTrue(errorMsg.contains("Shop operation failed for delivery :     "));
              });
      // Assert

    }
  }

  @Test
  @DisplayName("[사장님-주문접수 : 성공]")
  void acceptDelivery() throws JsonProcessingException {
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      var expected = deliveryList.get(idx);
      // Arrange
      expected.setDeliveryStatus(DeliveryStatus.NEW);
      var body = mapper.writeValueAsString(expected);
      var savedMono = deliveryRepository.save(expected);
      StepVerifier.create(savedMono)
          .expectNextMatches(
              saved -> {
                if (expected.getOrderTime().isAfter(expected.getPickupTime())) {
                  testClient
                      .put()
                      .uri("/api/delivery/accept")
                      .bodyValue(expected)
                      .exchange()
                      .expectStatus()
                      .isEqualTo(HttpStatus.BAD_REQUEST)
                      .expectBody(String.class);
                  return true;
                } else {
                  //
                  var responseBody =
                      testClient
                          .put()
                          .uri("/api/delivery/accept")
                          .bodyValue(expected)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(Delivery.class)
                          .consumeWith(
                              result -> {
                                var actual = result.getResponseBody();
                                assertEquals(expected, actual);
                              });
                  // Assert
                }
                return true;
              });
    }
  }

  @Test
  @DisplayName("[배달대행사-라이더배정: 성공]")
  void setDeliveryRider() {
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      var delivery = deliveryList.get(idx);
      delivery.setRiderId(null);
      var expected = deliveryRepository.save(delivery).block();
      // Act
      if (expected.getDeliveryStatus().equals(ACCEPTED)) {
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
  void setPickedUp() {
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var delivery = deliveryList.get(idx);
      var before = deliveryRepository.save(delivery).block();
      var expected = before.deepCopy();
      expected.setDeliveryStatus(before.getDeliveryStatus().getNext());
      //
      if (before.getDeliveryStatus().equals(ACCEPTED)
          && expected.getDeliveryStatus().equals(PICKED_UP)) {
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
            .expectBody(String.class)
            .returnResult();
      }
    }
  }

  @Test
  @DisplayName("[배달완료 이벤트 : 성공]")
  void setComplete() {
    for (int idx = 0; idx < deliveryList.size(); idx++) {
      // Arrange
      var delivery = deliveryList.get(idx);
      var expected = deliveryRepository.save(delivery).block();
      expected.setDeliveryStatus(expected.getDeliveryStatus().getNext());
      // Act
      if (expected.getDeliveryStatus().equals(PICKED_UP)) {
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

        // Act
      } else {
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
