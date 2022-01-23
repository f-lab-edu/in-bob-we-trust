package com.inbobwetrust.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.repository.DeliveryRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "restClient.proxy.shopUrl=http://DOESNOTEXIST",
      "restClient.proxy.agencyUrl=http://DOESNOTEXIST"
    })
@AutoConfigureWebTestClient
public class DeliveryControllerIntgrationTest2 {
  @Autowired WebTestClient testClient;
  @Autowired DeliveryRepository deliveryRepository;

  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  static Delivery makeDeliveryIsPickedUp(DeliveryStatus status) {
    return Delivery.builder()
        .orderId(LocalDateTime.now().toString())
        .customerId("customer-1234")
        .shopId("shop-1234")
        .address("서울시 강남구 삼성동 봉은사로 12-41")
        .deliveryStatus(status)
        .phoneNumber("01031583212")
        .orderTime(LocalDateTime.now())
        .pickupTime(LocalDateTime.now().plusMinutes(1))
        .build();
  }

  @DisplayName("[배달완료 여부 조회API]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument0={0}, Argument1={1}")
  @MethodSource("isPickedUp_methodSource")
  void isPickedUp_Test(Delivery delivery, boolean isPickedUp) throws JsonProcessingException {
    // Arrange
    deliveryRepository.save(delivery).block(Duration.ofSeconds(1));
    final String testUrl = "/api/delivery/is-picked-up/" + delivery.getId();
    // Act
    var actual =
        testClient
            .get()
            .uri(testUrl)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Boolean.class)
            .isEqualTo(isPickedUp);
  }

  static Stream<Arguments> isPickedUp_methodSource() {
    return Arrays.stream(DeliveryStatus.values())
        .map(
            status ->
                status.equals(DeliveryStatus.PICKED_UP)
                    ? Arguments.of(makeDeliveryIsPickedUp(status), true)
                    : Arguments.of(makeDeliveryIsPickedUp(status), false));
  }

}
