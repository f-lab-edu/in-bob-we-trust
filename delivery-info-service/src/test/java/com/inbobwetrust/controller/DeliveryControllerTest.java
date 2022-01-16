package com.inbobwetrust.controller;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.service.DeliveryService;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.inbobwetrust.controller.DeliveryController.MIN_PAGE;
import static com.inbobwetrust.controller.DeliveryController.MIN_SIZE;
import static org.mockito.Mockito.*;

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

  @Test
  void getDeliveries_success() {
    // Arrange
    int page = 0;
    int size = 10;
    var uri =
        UriComponentsBuilder.fromUri(URI.create(DELIVERY_URL))
            .queryParam("page", page)
            .queryParam("size", size)
            .buildAndExpand()
            .toUri();
    // Stub
    var returnDeliveryList = makeValidDeliveries(size * 10);
    when(deliveryService.findAll(isA(PageRequest.class)))
        .thenReturn(Flux.fromIterable(returnDeliveryList.subList(page, size)));
    // Act
    var response =
        testClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Delivery.class)
            .hasSize(10)
            .returnResult()
            .getResponseBody();
    // Assert
    LOG.info("Response is :      {}", response);
    verify(deliveryService, times(1)).findAll(any());
  }

  static Stream<Arguments> pageables() {
    // 모든 page 와 size가 null 일 경우를 따지는 모든 경우의수를... 봐야한다.
    return Stream.of(
        Arguments.of(null, MIN_SIZE, MIN_PAGE, MIN_SIZE),
        Arguments.of(MIN_PAGE - 1, MIN_SIZE, MIN_PAGE, MIN_SIZE),
        Arguments.of(MIN_PAGE, MIN_SIZE, MIN_PAGE, MIN_SIZE),
        Arguments.of(MIN_PAGE + 1, MIN_SIZE, MIN_PAGE + 1, MIN_SIZE),
        Arguments.of(MIN_PAGE, null, MIN_PAGE, MIN_SIZE),
        Arguments.of(MIN_PAGE, MIN_SIZE - 1, MIN_PAGE, MIN_SIZE),
        Arguments.of(MIN_PAGE, MIN_SIZE, MIN_PAGE, MIN_SIZE),
        Arguments.of(MIN_PAGE, MIN_SIZE + 1, MIN_PAGE, MIN_SIZE + 1));
  }

  @ParameterizedTest
  @DisplayName("size default 값 설정테스트, deliveryService에 제대로 전달되는지 확인한다.")
  @MethodSource("pageables")
  void getDeliveries_success_pageable(
      Integer page, Integer size, Integer expectedPage, Integer expectedSize) {
    // Arrange
    // Stub
    when(deliveryService.findAll(any(PageRequest.class)))
        .thenReturn(Flux.fromIterable(makeValidDeliveries(MIN_SIZE)));
    var uri =
        UriComponentsBuilder.fromUri(URI.create(DELIVERY_URL))
            .queryParam("page", page)
            .queryParam("size", size)
            .buildAndExpand()
            .toUri();
    // Act
    var response = testClient.get().uri(uri).exchange().expectStatus().isOk();
    // Assert
    verify(deliveryService, times(1)).findAll(PageRequest.of(expectedPage, expectedSize));
  }

  private List<Delivery> makeValidDeliveries(int count) {
    var deliveries = new ArrayList<Delivery>();
    for (int i = 1; i <= count; i++) {
      var dlvry = makeValidDelivery();
      dlvry.setOrderId("order-" + i);
      deliveries.add(dlvry);
    }
    return deliveries;
  }

  @Test
  void getDelivery() {
    // Arrange
    var delivery = makeValidDelivery();
    delivery.setId("delivery-1234");
    // Stub
    when(deliveryService.findById(delivery.getId())).thenReturn(Mono.just(delivery));
    // Act
    var result =
        testClient
            .get()
            .uri(DELIVERY_URL + "/{id}", delivery.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();
    // Assert
    Assertions.assertEquals(delivery, result);
  }

  @Test
  void acceptDelivery() {
    // Arrange
    var delivery = makeValidDelivery();
    delivery.setId("delivery-1234");
    // Stub
    when(deliveryService.acceptDelivery(any(Delivery.class))).thenReturn(Mono.just(delivery));
    // Act
    var result =
        testClient
            .put()
            .uri(DELIVERY_URL + "/accept")
            .bodyValue(delivery)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();
    // Assert
    Assertions.assertEquals(delivery, result);
  }

  @DisplayName("[배달완료여부 조회API]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("isPickedUpSource")
  void isPickedUp(boolean isPickedUp) {
    // Arrange
    var delivery = makeValidDelivery();
    // Stub
    when(deliveryService.isPickedUp(anyString())).thenReturn(Mono.just(isPickedUp));
    // Act
    testClient
        .get()
        .uri(DELIVERY_URL + "/is-picked-up/" + delivery.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Boolean.class)
        .isEqualTo(isPickedUp);
  }

  static Stream<Arguments> isPickedUpSource() {
    return Stream.of(Arguments.of(true), Arguments.of(false));
  }
}
