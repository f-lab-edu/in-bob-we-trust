package com.inbobwetrust.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.repository.DeliveryRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@ContextConfiguration
public class DeliveryServiceImplTest {

  @Container
  static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");

  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Autowired private DeliveryService deliveryService;

  @Autowired private DeliveryRepository deliveryRepository;

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

  @Test
  @DisplayName("New 상태에서 Accepted로 저장시 Accepted 로 저장되는지 확인하기 확인하기")
  void acceptDeliveryTest() throws JsonProcessingException {
    // given
    var delivery = makeDeliveryIsPickedUp(DeliveryStatus.NEW);
    delivery.setId(LocalDateTime.now().toString());
    var acceptDelivery = delivery.deepCopy();
    acceptDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    // when
    deliveryRepository.save(delivery).block();
    deliveryService.acceptDelivery(acceptDelivery).block();
    // then
    StepVerifier.create(deliveryRepository.findById(delivery.getId()))
        .expectNextMatches(del -> del.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED))
        .verifyComplete();
  }
}
