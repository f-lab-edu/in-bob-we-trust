package com.inbobwetrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class DatabaseConnectionFailoverTest {
  static final Logger LOG = LoggerFactory.getLogger(DatabaseConnectionFailoverTest.class);

  @Autowired WebTestClient testClient;

  @Autowired DeliveryRepository deliveryRepository;

  @Container
  public static GenericContainer<?> mongodb =
      new GenericContainer<>("mongo:latest")
          .withEnv("MONGO_INITDB_DATABASE", "delivery")
          .withExposedPorts(27017)
          .waitingFor(
              new HttpWaitStrategy().forPort(27017).withStartupTimeout(Duration.ofSeconds(10)));

  @DynamicPropertySource
  static void datasourceProperties(DynamicPropertyRegistry registry) throws InterruptedException {
    mongodb.start();
    var hostPort = mongodb.getMappedPort(27017);
    var hostIpAddress = mongodb.getContainerIpAddress();
    registry.add("spring.data.mongodb.host", () -> hostIpAddress);
    registry.add("spring.data.mongodb.port", () -> hostPort);
    LOG.info("------------------------------------------------------");
    LOG.info("spring.data.mongodb.host : {}", hostIpAddress);
    LOG.info("spring.data.mongodb.port : {}", hostPort);
  }

  private String proxyShopUrl = "/relay/v1/shop";
  private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Test
  void hi() throws InterruptedException {
    var stream =
        deliveryRepository.save(
            Delivery.builder()
                .orderId("order-1234")
                .customerId("customer-1234")
                .address("서울시 강남구 삼성동 봉은사로 12-41")
                .phoneNumber("01031583212")
                .orderTime(LocalDateTime.now())
                .build());
    StepVerifier.create(stream)
        .expectNextMatches(delivery -> delivery.getOrderId().equals("order-1234"))
        .verifyComplete();
    deliveryRepository.findAll().log().blockLast();

    for (int i = 0; i < 10000; i++) {
      var f =
          deliveryRepository.save(
              Delivery.builder()
                  .orderId("order-1234")
                  .customerId("customer-1234")
                  .address("서울시 강남구 삼성동 봉은사로 12-41")
                  .phoneNumber("01031583212")
                  .orderTime(LocalDateTime.now())
                  .build());
      StepVerifier.create(f)
          .expectNextMatches(de -> de.getOrderId().equals("order-1234"))
          .verifyComplete();
    }

    while (true) {
      Thread.sleep(1000);
      LOG.info("..");
    }
  }
}
