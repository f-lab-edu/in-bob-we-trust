package com.inbobwetrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.repository.primary.DeliveryRepository;
import com.inbobwetrust.repository.secondary.SecondaryDeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.inbobwetrust.service.DeliveryService.FIXED_DELAY;
import static com.inbobwetrust.service.DeliveryService.MAX_ATTEMPTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("integrationtest")
public class DatabaseConnectionFailoverTest {
  static final Logger LOG = LoggerFactory.getLogger(DatabaseConnectionFailoverTest.class);
  private static final int MONGO_PORT = 27017;
  private String proxyShopUrl = "/relay/v1/shop";
  private String ADD_DELIVERY_URI = "/api/delivery";

  private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Autowired WebTestClient testClient;

  @SpyBean DeliveryRepository deliveryRepository;

  @SpyBean SecondaryDeliveryRepository secondaryDeliveryRepository;

  @Container public GenericContainer<?> primaryMongo;

  @Container public GenericContainer<?> secondaryMongo;

  @BeforeEach
  void setUp() {
    primaryMongo = makeMongoDb("primary", 12345);
    if (!primaryMongo.isRunning()) {
      primaryMongo.start();
    }
    secondaryMongo = makeMongoDb("secondary", 12346);
    if (!secondaryMongo.isRunning()) {
      secondaryMongo.start();
    }
    assertTrue(primaryMongo.isRunning() && secondaryMongo.isRunning());
    deliveryRepository.deleteAll().block(Duration.ofSeconds(1));
    secondaryDeliveryRepository.deleteAll().block(Duration.ofSeconds(1));
  }

  GenericContainer makeMongoDb(String name, int hostPort) {
    return new FixedHostPortGenericContainer("mongo:latest")
        .withFixedExposedPort(hostPort, MONGO_PORT)
        .withEnv("MONGO_INITDB_DATABASE", "inbob")
        .waitingFor(
            new HttpWaitStrategy().forPort(MONGO_PORT).withStartupTimeout(Duration.ofSeconds(10)));
  }

  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @DisplayName("[DB연결 테스트] Docker Container 작동여부 확인, 포트 매핑 다른지 확인하기")
  @MethodSource("makeDeliveryArgument")
  void databaseConnectionFailoverTest(final Delivery delivery)
      throws InterruptedException, SQLException {
    assertTrue(secondaryMongo.isRunning());
    assertTrue(primaryMongo.isRunning());
    Assertions.assertNotEquals(
        secondaryMongo.getMappedPort(MONGO_PORT), primaryMongo.getMappedPort(MONGO_PORT));
  }

  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @DisplayName("[Primary & Secondary DB Connection 테스트]")
  @MethodSource("makeDeliveryArgument")
  void databaseConnectionFailoverTest2(final Delivery delivery)
      throws InterruptedException, SQLException {
    // given
    var startCountPrimary = deliveryRepository.count().block();
    var startCountSecondary = secondaryDeliveryRepository.count().block();

    var oneDeliveries = List.of(makeADelivery());
    var fourDeliveries =
        List.of(makeADelivery(), makeADelivery(), makeADelivery(), makeADelivery());

    // when
    deliveryRepository.saveAll(oneDeliveries).collectList().block();

    secondaryDeliveryRepository.saveAll(fourDeliveries).collectList().block();

    // then
    var primaryStream = deliveryRepository.findAll().collectList();
    StepVerifier.create(primaryStream)
        .expectNextMatches(pr -> pr.size() == (startCountPrimary + oneDeliveries.size()))
        .verifyComplete();

    var secondaryStream = secondaryDeliveryRepository.findAll().collectList();
    StepVerifier.create(secondaryStream)
        .expectNextMatches(sc -> sc.size() == (startCountSecondary + fourDeliveries.size()))
        .verifyComplete();
  }

  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @DisplayName("[DB Failover 테스트] Primary 데이터베이스를 다운시키면 Secondary에 저장된다.")
  @MethodSource("makeDeliveryArgument")
  void databaseConnectionRetryTest(Delivery delivery) {
    LOG.info("primaryMongo is Running : {}", primaryMongo.isRunning());
    LOG.info("secondaryMongo is Running : {}", secondaryMongo.isRunning());
    // given
    primaryMongo.stop();
    delivery.setId(null);
    var responseTime_minThreshold = FIXED_DELAY.multipliedBy(MAX_ATTEMPTS + 1).toMillis();
    var startTime = System.currentTimeMillis();

    // when
    var savedDelivery =
        testClient
            .post()
            .uri(ADD_DELIVERY_URI)
            .bodyValue(delivery)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Delivery.class)
            .returnResult()
            .getResponseBody();

    // then
    var endTime = System.currentTimeMillis();
    var executionTime = endTime - startTime;
    assertEquals(savedDelivery.getOrderId(), delivery.getOrderId());
    assertTrue(responseTime_minThreshold < executionTime);

    verify(deliveryRepository, times(2)).save(any());
    verify(secondaryDeliveryRepository, times(1)).save(any());
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

  @Test
  void spyTest() {
    deliveryRepository.save(makeADelivery()).block();
    verify(deliveryRepository, times(1)).save(any());
  }

  Delivery makeADelivery() {
    return Delivery.builder()
        .orderId("order-")
        .riderId("rider-")
        .agencyId("agency-")
        .shopId("shop-")
        .customerId("customer-")
        .address("서울시 강남구 삼성동 봉은사로 12-41 / number")
        .phoneNumber("01031583212-")
        .deliveryStatus(DeliveryStatus.NEW)
        .orderTime(LocalDateTime.now())
        .pickupTime(LocalDateTime.now().plusMinutes(30))
        .finishTime(LocalDateTime.now().plusMinutes(60))
        .build();
  }
}
