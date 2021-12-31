package com.inbobwetrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.repository.primary.DeliveryRepository;
import com.inbobwetrust.repository.secondary.SecondaryDeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class DatabaseConnectionFailoverTest {
  static final Logger LOG = LoggerFactory.getLogger(DatabaseConnectionFailoverTest.class);

  @Autowired WebTestClient testClient;

  @Autowired DeliveryRepository primaryDeliveryRepository;

  @Autowired SecondaryDeliveryRepository secondaryDeliveryRepository;

  @Autowired ReactiveMongoDatabaseFactory mongoProperties;

  @Container public static GenericContainer<?> primaryMongo = makeMongoDb("primary");

  @Container public static GenericContainer<?> secondaryMongo = makeMongoDb("secondary");

  static GenericContainer makeMongoDb(String name) {
    return new GenericContainer<>("mongo:latest")
        .withCreateContainerCmdModifier(cmd -> cmd.withName(name))
        .withEnv("MONGO_INITDB_DATABASE", "inbob")
        .withExposedPorts(27017)
        .waitingFor(
            new HttpWaitStrategy().forPort(27017).withStartupTimeout(Duration.ofSeconds(10)));
  }

  @DynamicPropertySource
  static void datasourceProperties(DynamicPropertyRegistry registry) throws InterruptedException {

    primaryMongo.start();
    var hostPort = primaryMongo.getMappedPort(27017);
    var primaryUriString = String.format("mongodb://localhost:%d/inbob", hostPort);
    registry.add("spring.data.mongodb.primary.uri", () -> primaryUriString);

    secondaryMongo.start();
    var secondaryPort = secondaryMongo.getMappedPort(27017);
    registry.add(
        "spring.data.mongodb.secondary.uri",
        () -> "mongodb://localhost:" + secondaryPort + "/inbob");
  }

  private String proxyShopUrl = "/relay/v1/shop";
  private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @ParameterizedTest
  @DisplayName("[DB연결 테스트] Docker Container 작동여부 확인하기")
  @MethodSource("makeDeliveryArgument")
  void databaseConnectionFailoverTest(Delivery delivery) throws InterruptedException, SQLException {
    // given
    var startCount = primaryDeliveryRepository.count().block(Duration.ofSeconds(1));
    var finishCount = primaryDeliveryRepository.count().block(Duration.ofSeconds(1));
    // when
    
    // then
  }

  static Stream<Arguments> makeDeliveryArgument() {
    return Stream.of(Arguments.of(makeDelivery()));
  }

  static Delivery makeDelivery() {
    return Delivery.builder()
        .orderId("order-")
        .shopId("shop-")
        .customerId("customer-")
        .address("서울시 강남구 삼성동 봉은사로 12-41 / number")
        .phoneNumber("01031583212-")
        .orderTime(LocalDateTime.now())
        .build();
  }
}
