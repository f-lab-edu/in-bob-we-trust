package com.inbobwetrust.messagequeue;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.repository.DeliveryRepository;
import groovy.util.logging.Slf4j;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@ContextConfiguration
@Slf4j
public class RabbitTest {

  @Autowired WebTestClient testClient;

  @Autowired DeliveryRepository deliveryRepository;

  @Test
  void sendDeliveryThroughRabbit() throws InterruptedException {
    for (int i = 0; i < 1000; i++) {
      System.out.println("running...");
      var deliveryOne = makeValidSetPickUpDelivery(i);
      sendDeliveryMessage("/test/delivery", deliveryOne);
    }
    Thread.sleep(1000L);

    var stream = this.deliveryRepository.findAll();
    StepVerifier.create(stream).expectNextCount(1000L).verifyComplete();
  }

  private Delivery makeValidSetPickUpDelivery(int id) {
    return Delivery.builder()
        .id("id-1234==" + id)
        .orderId("order1==" + id)
        .shopId("shop-1234==" + id)
        .riderId("rider-1234==" + id)
        .agencyId("agency-1234==" + id)
        .customerId("customer-1234==" + id)
        .address("서울시 강남구...==" + id)
        .phoneNumber("01031583977==" + id)
        .deliveryStatus(DeliveryStatus.ACCEPTED)
        .orderTime(LocalDateTime.now().minusMinutes(1))
        .pickupTime(LocalDateTime.now().plusMinutes(30))
        .finishTime(LocalDateTime.now().plusMinutes(60))
        .build();
  }

  private void sendDeliveryMessage(String endpoint, Delivery delivery) {
    this.testClient
        .post()
        .uri("/test/delivery")
        .bodyValue(delivery)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody();
  }
}
