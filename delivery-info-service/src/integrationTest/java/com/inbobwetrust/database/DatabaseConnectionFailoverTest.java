package com.inbobwetrust.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.repository.primary.PrimaryDeliveryRepository;
import com.inbobwetrust.repository.secondary.SecondaryDeliveryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.inbobwetrust.service.DeliveryService.FIXED_DELAY;
import static com.inbobwetrust.service.DeliveryService.MAX_ATTEMPTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("integration")
public class DatabaseConnectionFailoverTest {
  static final Logger LOG = LoggerFactory.getLogger(DatabaseConnectionFailoverTest.class);
  private String proxyShopUrl = "/relay/v1/shop";
  private String ADD_DELIVERY_URI = "/api/delivery";
  private static final String DEFAULT_MONGO_DATABASE = "inbob";
  private static final String MONGO_IMAGE = "mongo:latest";

  private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Autowired WebTestClient testClient;

  @SpyBean
  PrimaryDeliveryRepository primaryDeliveryRepository;

  @SpyBean SecondaryDeliveryRepository secondaryDeliveryRepository;

  @Container static GenericContainer<?> primaryMongo = makeMongoDb();

  @Container static GenericContainer<?> secondaryMongo = makeMongoDb();

  @BeforeEach
  void setUp() {
    primaryMongo.start();
    secondaryMongo.start();
    assertTrue(primaryMongo.isRunning() && secondaryMongo.isRunning());
    primaryDeliveryRepository.deleteAll().block(Duration.ofSeconds(1));
    secondaryDeliveryRepository.deleteAll().block(Duration.ofSeconds(1));
  }

  static GenericContainer makeMongoDb() {
    return new GenericContainer(MONGO_IMAGE)
        .withExposedPorts(MongoProperties.DEFAULT_PORT)
        .withEnv("MONGO_INITDB_DATABASE", DEFAULT_MONGO_DATABASE)
        .waitingFor(
            new HttpWaitStrategy()
                .forPort(MongoProperties.DEFAULT_PORT)
                .withStartupTimeout(Duration.ofSeconds(10)));
  }

  @DynamicPropertySource
  static void datasourceProperties(DynamicPropertyRegistry registry) throws InterruptedException {
    assertTrue(primaryMongo.isRunning());
    registry.add("spring.data.mongodb.primary.uri", () -> extractSimpleMongoUri(primaryMongo));

    assertTrue(secondaryMongo.isRunning());
    registry.add("spring.data.mongodb.secondary.uri", () -> extractSimpleMongoUri(secondaryMongo));
  }

  private static Object extractSimpleMongoUri(GenericContainer<?> primaryMongo) {
    return String.format(
        "mongodb://%s:%d/%s",
        primaryMongo.getHost(),
        primaryMongo.getMappedPort(MongoProperties.DEFAULT_PORT),
        DEFAULT_MONGO_DATABASE);
  }

  @AfterEach
  void tearDown() {
    primaryMongo.stop();
    secondaryMongo.stop();
  }

  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @DisplayName("[DB Failover 테스트] Primary 데이터베이스를 다운시키면 Secondary에 저장된다.")
  @MethodSource("makeDeliveryArgument")
  void databaseConnectionRetryTest(Delivery delivery) throws JsonProcessingException {
    // given
    delivery.setId(null);

    var responseTime_minThreshold = FIXED_DELAY.multipliedBy(MAX_ATTEMPTS + 1).toMillis();

    primaryMongo.stop();

    stubFor(
        post(urlPathMatching(proxyShopUrl + "/.*"))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(delivery))));
    // when
    var startTime = System.currentTimeMillis();
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
    var endTime = System.currentTimeMillis();

    // then
    var executionTime = endTime - startTime;
    assertTrue(responseTime_minThreshold < executionTime);

    assertEquals(savedDelivery.getOrderId(), delivery.getOrderId());

    verify(primaryDeliveryRepository, times(1)).save(any());
    verify(secondaryDeliveryRepository, times(1)).save(any());
  }

  static Stream<Arguments> makeDeliveryArgument() {
    return Stream.of(Arguments.of(makeDelivery()));
  }

  static Delivery makeDelivery() {
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
