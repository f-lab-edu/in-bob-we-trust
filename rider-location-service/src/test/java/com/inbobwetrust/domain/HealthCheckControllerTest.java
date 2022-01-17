package com.inbobwetrust.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import groovy.util.logging.Slf4j;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(HealthCheckController.class)
@AutoConfigureWebTestClient
@Slf4j
class HealthCheckControllerTest {

  @Autowired WebTestClient testClient;

  @Test
  @DisplayName("Ping Hello World")
  void helloWorld() {
    // given
    var uri = "";
    // when
    testClient
        .get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .consumeWith(
            response -> {
              assertTrue(
                  Objects.requireNonNull(response.getResponseBody())
                      .toLowerCase()
                      .contains("hello world"));
            });
    // then
  }
}
