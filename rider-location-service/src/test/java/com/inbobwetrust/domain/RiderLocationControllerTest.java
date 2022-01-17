package com.inbobwetrust.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(RiderLocationController.class)
@AutoConfigureWebTestClient
class RiderLocationControllerTest {

  @Autowired WebTestClient testClient;

  @MockBean RiderLocationService riderLocationService;

  String riderLocationMapping = "/rider/location";

  @Test
  void updateLocationPut_test() {
    // given
    var location = new RiderLocation("rider-1234", "delivery-1234", 23.0f, 190f);
    // when
    when(riderLocationService.setIfPresent(any(RiderLocation.class)))
        .thenReturn(Mono.just(Boolean.TRUE));
    var result =
        testClient
            .put()
            .uri(riderLocationMapping)
            .bodyValue(location)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(Boolean.class)
            .getResponseBody();
    // then
    StepVerifier.create(result).expectNextMatches(Boolean::booleanValue).verifyComplete();
  }

  @Test
  void updateLocationPost_test() {
    // given
    var location = new RiderLocation("rider-1234", "delivery-1234", 23.0f, 190f);
    // when
    when(riderLocationService.setIfPresent(any(RiderLocation.class)))
        .thenReturn(Mono.just(Boolean.TRUE));
    var result =
        testClient
            .post()
            .uri(riderLocationMapping)
            .bodyValue(location)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(Boolean.class)
            .getResponseBody();
    // then
    StepVerifier.create(result).expectNextMatches(Boolean::booleanValue).verifyComplete();
  }
}
