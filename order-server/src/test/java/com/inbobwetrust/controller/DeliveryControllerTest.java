package com.inbobwetrust.controller;

import static org.mockito.Mockito.*;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.service.DeliveryService;
import groovy.util.logging.Slf4j;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(DeliveryController.class)
@AutoConfigureWebTestClient
@Slf4j
public class DeliveryControllerTest {
  static final Logger LOG = LoggerFactory.getLogger(DeliveryControllerTest.class);
  @Autowired WebTestClient testClient;
  @MockBean DeliveryService deliveryService;

  final String DELIVERY_URL = "/api/delivery";
  final String FIELD_IS_REQUIRED = "필수 입력값입니다.";

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

  private Delivery makeInvalidDelivery() {
    return Delivery.builder().build();
  }

  @Test
  void addDelivery_invalid_delivery_information() {
    // given
    var delivery = makeInvalidDelivery();
    // when

    // then
    testClient
        .post()
        .uri(DELIVERY_URL)
        .bodyValue(delivery)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(String.class)
        .consumeWith(
            response -> {
              var responseBody = response.getResponseBody();
              responseBody.contains(FIELD_IS_REQUIRED);
            });
    verify(deliveryService, times(0)).addDelivery(delivery);
  }

  @Test
  void addDelivery_successful() {
    // given
    var expected = makeValidDelivery();
    // when
    when(deliveryService.addDelivery(isA(Delivery.class))).thenReturn(Mono.just(expected));
    // then
    var actual =
        testClient
            .post()
            .uri(DELIVERY_URL)
            .bodyValue(expected)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();
    // asesrt
    expected.setOrderTime(actual.getOrderTime());
    Assertions.assertEquals(expected, actual);
    verify(deliveryService, times(1)).addDelivery(any());
  }

  @Test
  void setDeliveryRider_successful() {
    // given
    var expected = makeValidDelivery();
    // when
    when(deliveryService.setDeliveryRider(isA(Delivery.class))).thenReturn(Mono.just(expected));
    // then
    var actual =
        testClient
            .put()
            .uri(DELIVERY_URL + "/rider")
            .bodyValue(expected)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();
    // asesrt
    expected.setOrderTime(actual.getOrderTime());
    Assertions.assertEquals(expected, actual);
    verify(deliveryService, times(1)).setDeliveryRider(any());
  }

  @Test
  void setDeliveryRider_fail() {
    // given
    var expected = makeInvalidDelivery();
    // when
    when(deliveryService.setDeliveryRider(isA(Delivery.class))).thenReturn(Mono.just(expected));
    // then
    var actual =
        testClient
            .put()
            .uri(DELIVERY_URL + "/rider")
            .bodyValue(expected)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    // asesrt
    verify(deliveryService, times(0)).setDeliveryRider(any());
  }

  @Test
  void setPickedUp() {
    // Arrange
    var delivery = makeValidDelivery();
    // Stub
    when(deliveryService.setPickedUp(isA(Delivery.class))).thenReturn(Mono.just(delivery));
    // Act
    var actual =
        testClient
            .put()
            .uri(DELIVERY_URL + "/pickup")
            .bodyValue(delivery)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();
    // Assert
    Assertions.assertEquals(delivery, actual);
    verify(deliveryService, times(1)).setPickedUp(delivery);
  }

  @Test
  void setPickedUp_fail_bean_validation_fail() {
    // Arrange
    var delivery = makeValidDelivery();
    delivery.setShopId(null);
    delivery.setOrderTime(null);
    // Stub
    when(deliveryService.setPickedUp(isA(Delivery.class))).thenReturn(Mono.just(delivery));
    // Act
    var response =
        testClient
            .put()
            .uri(DELIVERY_URL + "/pickup")
            .bodyValue(delivery)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    // Assertk
    Assertions.assertTrue(response.contains("필수 입력값입니다."));
    verify(deliveryService, times(0)).setPickedUp(delivery);
  }

  @Test
  void setComplete_success() {
    // Arrange
    var request = makeValidDelivery();
    // Stub
    when(deliveryService.setComplete(isA(Delivery.class))).thenReturn(Mono.just(request));
    // Act
    var response =
        testClient
            .put()
            .uri(DELIVERY_URL + "/complete")
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();
    // Assert
    LOG.debug("Response is :      {}", response);
    Assertions.assertEquals(request, response);
    verify(deliveryService, times(1)).setComplete(any(Delivery.class));
  }

  @Test
  void setComplete_fail_bean_validation() {
    // Arrange
    var delivery = makeValidDelivery();
    delivery.setOrderId(null);
    delivery.setShopId(null);
    // Stub
    when(deliveryService.setComplete(isA(Delivery.class))).thenReturn(Mono.just(delivery));
    // Act
    var response =
        testClient
            .put()
            .uri(DELIVERY_URL + "/complete")
            .bodyValue(delivery)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    // Assert
    LOG.info("Response is :      {}", response);
    Assertions.assertTrue(response.contains("필수 입력값입니다."));
    verify(deliveryService, times(0)).setComplete(any(Delivery.class));
  }
}
