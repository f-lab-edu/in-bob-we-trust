package com.inbobwetrust.actuator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("loadTest")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsedActuatorEndpointTest {
  @Autowired WebTestClient testClient;

  @Autowired ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Container public static GenericContainer<?> primaryMongo = makeMongoDb();

  @Container public static GenericContainer<?> secondaryMongo = makeMongoDb();

  static GenericContainer makeMongoDb() {
    return new GenericContainer<>("mongo:latest")
        .withEnv("MONGO_INITDB_DATABASE", "inbob")
        .withExposedPorts(MongoProperties.DEFAULT_PORT)
        .waitingFor(
            new HttpWaitStrategy()
                .forPort(MongoProperties.DEFAULT_PORT)
                .withStartupTimeout(Duration.ofSeconds(10)));
  }

  @Test
  @DisplayName("[지정된 스프링 Profile에서 Actuator 엔드포인트 정상작동 테스트] actuator/health")
  void actuatorHealthTest() throws InterruptedException {
    // given
    var actuatorHealth = "/actuator/health";
    // when
    var response =
        testClient
            .get()
            .uri(actuatorHealth)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Map.class)
            .returnResult()
            .getResponseBody();
    // then
    Assertions.assertTrue(response.containsKey("status"));
    Assert.assertEquals(response.get("status"), "UP");
  }

  @Test
  @DisplayName("[지정된 스프링 Profile에서 Actuator 엔드포인트 정상작동 테스트] actuator/info")
  void actuatorInfoTest() throws InterruptedException {
    // given
    var actuatorHealth = "/actuator/info";
    // when
    var response =
        testClient
            .get()
            .uri(actuatorHealth)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Object.class);
  }

  @Test
  @DisplayName("[지정된 스프링 Profile에서 Actuator 엔드포인트 정상작동 테스트] actuator/metrics/process.cpu.usage")
  void actuatorMetricsProcessCpuUsageTest() throws InterruptedException, JSONException {
    // given
    var endPoint = "/actuator/metrics/process.cpu.usage";
    // when
    var response =
        testClient
            .get()
            .uri(endPoint)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Map.class)
            .returnResult()
            .getResponseBody();
    // then
    Objects.nonNull(response);

    Assertions.assertEquals("process.cpu.usage", response.get("name"));

    var measurements = (List<Map>) response.get("measurements");
    Assertions.assertEquals(1, measurements.size());
    var aMeasurement = measurements.get(0);
    Assertions.assertEquals("VALUE", aMeasurement.get("statistic"));
    Assertions.assertTrue(((double) aMeasurement.get("value")) >= 0);
  }
}
