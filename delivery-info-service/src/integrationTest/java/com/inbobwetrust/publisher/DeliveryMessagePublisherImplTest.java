package com.inbobwetrust.publisher;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@ContextConfiguration
public class DeliveryMessagePublisherImplTest {

  @Value("messageQueue.exchange.shop")
  private String shopExchange;

  @Value("messageQueue.exchange.agency")
  private String agencyExchange;

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  DeliveryRepository deliveryRepository;

  @Container
  static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");

  @SpyBean
  AmqpTemplate amqpTemplate;

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.rabbitmq.host", container::getContainerIpAddress);
    registry.add("spring.rabbitmq.port", container::getAmqpPort);
  }

  @Test
  void sendAddDeliveryEventTest() throws InterruptedException {
    // given
    var delivery = makeValidDelivery();
    // when
    var resBody = this.webTestClient
        .post()
        .uri("/api/delivery")
        .bodyValue(delivery)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Delivery.class)
        .returnResult()
        .getResponseBody();

    Thread.sleep(1000L);
    // then
    var savedDelivery = deliveryRepository.findAll().blockLast();
    delivery.setId(savedDelivery.getId());
    assertTrue(delivery.getShopId().equals(savedDelivery.getShopId()));
    assertTrue(delivery.getCustomerId().equals(savedDelivery.getCustomerId()));
    verify(amqpTemplate, times(1)).convertAndSend(eq(shopExchange), any(Delivery.class));
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
