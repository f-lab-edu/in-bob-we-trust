package com.inbobwetrust.controller;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.service.DeliveryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@WebFluxTest(DeliveryController.class)
@AutoConfigureWebTestClient
public class DeliveryControllerTest {

  @Autowired WebTestClient testClient;
  @MockBean DeliveryService deliveryService;

  final String DELIVERY_URL = "/api/delivery";
  final String FIELD_IS_REQUIRED = "필수 입력값입니다.";

  private Delivery makeValidDelivery() {
    return Delivery.builder()
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

    Assertions.assertEquals(expected, actual);
    verify(deliveryService, times(1)).addDelivery(any());
  }
}
