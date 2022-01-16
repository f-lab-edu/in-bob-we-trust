package com.inbobwetrust.exception;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.inbobwetrust.controller.DeliveryController;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.service.DeliveryService;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(DeliveryController.class)
@AutoConfigureWebTestClient
public class ExceptionHandlerAdviceTest {
  @Autowired WebTestClient testClient;
  @MockBean DeliveryService deliveryService;

  final String DELIVERY_URL = "/api/delivery";

  static Stream<Arguments> exceptionClassList() throws NoSuchMethodException {
    return Stream.of(
        Arguments.of(new IllegalArgumentException(), HttpStatus.BAD_REQUEST),
        Arguments.of(new IllegalStateException(), HttpStatus.BAD_REQUEST),
        Arguments.of(new DeliveryNotFoundException(), HttpStatus.BAD_REQUEST),
        Arguments.of(new RelayClientException("ClientException"), HttpStatus.EXPECTATION_FAILED),
        Arguments.of(new RelayServerException("ServerException"), HttpStatus.EXPECTATION_FAILED),
        Arguments.of(new RuntimeException(), HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @ParameterizedTest
  @MethodSource("exceptionClassList")
  void handleExceptionHandlerTests(Throwable throwable, HttpStatus httpStatus) {
    // Arrange
    var delivery = makeValidDelivery();
    delivery.setId("delivery-1234");
    // Stub
    when(deliveryService.findById(delivery.getId())).thenThrow(throwable);
    // Act
    testClient
        .get()
        .uri(DELIVERY_URL + "/{id}", delivery.getId())
        .exchange()
        .expectStatus()
        .isEqualTo(httpStatus);
    // Assert
  }

  @Test
  void handleWebExchangeBindingError() {
    // Arrange

    // Stub
    when(deliveryService.findById(anyString())).thenReturn(Mono.just(makeValidDelivery()));
    // Act
    testClient
        .post()
        .uri(DELIVERY_URL)
        .bodyValue(Delivery.builder().build())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST.value());
    // Assert
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
}
