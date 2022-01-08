package com.inbobwetrust.actuator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureMetrics
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("loadtest")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsedActuatorEndpointTest {
  @Autowired WebTestClient testClient;

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

  @Test
  @DisplayName("[지정된 스프링 Profile에서 Actuator 엔드포인트 정상작동 테스트] actuator/prometheus")
  void actuatorPrometheusTest() throws InterruptedException, JSONException {
    // when
    var response =
        testClient
            .get()
            .uri("/actuator/prometheus")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();
    // then
    Objects.nonNull(response);
    Assertions.assertTrue(response.contains("process_cpu_usage"));
    Assertions.assertTrue(response.contains("jvm_classes_loaded_classes"));
  }
}
