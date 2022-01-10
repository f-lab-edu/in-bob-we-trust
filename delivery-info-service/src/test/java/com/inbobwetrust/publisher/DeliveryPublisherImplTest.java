package com.inbobwetrust.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.RelayClientException;
import com.inbobwetrust.exception.RelayServerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeliveryPublisherImplTest {
  @Autowired DeliveryPublisherImpl deliveryPublisher;

  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Test
  void sendAddDeliveryEvent() throws JsonProcessingException {
    // given
    var delivery = makeValidDelivery();
    stubFor(
        post(urlPathEqualTo("/relay/v1/shop/" + delivery.getShopId()))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(delivery))));
    // when
    var response = deliveryPublisher.sendAddDeliveryEvent(delivery);
    // then
    StepVerifier.create(response)
        .consumeNextWith(next -> Assertions.assertEquals(next, delivery))
        .verifyComplete();
    WireMock.verify(1, postRequestedFor(urlPathEqualTo("/relay/v1/shop/" + delivery.getShopId())));
  }

  @Test
  void sendAddDeliveryEvent_4xx_isClientError() {
    // given
    var delivery = makeValidDelivery();
    stubFor(
        post(urlPathEqualTo("/relay/v1/shop/" + delivery.getShopId()))
            .willReturn(aResponse().withStatus(404)));
    // when
    var response = deliveryPublisher.sendAddDeliveryEvent(delivery);
    // then
    StepVerifier.create(response)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof RelayClientException);
              assertTrue(err.getMessage().contains("Push Event failed for delivery"));
              return true;
            })
        .verify();
    WireMock.verify(1, postRequestedFor(urlPathEqualTo("/relay/v1/shop/" + delivery.getShopId())));
  }

  @Test
  void sendAddDeliveryEvent_5xx_isServerError() throws JsonProcessingException {
    // given
    var delivery = makeValidDelivery();
    stubFor(
        post(urlPathEqualTo("/relay/v1/shop/" + delivery.getShopId()))
            .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    // when
    var response = deliveryPublisher.sendAddDeliveryEvent(delivery);
    // then
    StepVerifier.create(response)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof RelayServerException);
              assertTrue(err.getMessage().contains("Shop operation failed for delivery :     "));
              return true;
            })
        .verify();
    WireMock.verify(1, postRequestedFor(urlPathEqualTo("/relay/v1/shop/" + delivery.getShopId())));
  }

  @Test
  void sendSetRiderEvent_success() throws JsonProcessingException {
    // given
    var delivery = makeValidDelivery();
    stubFor(
        post(urlPathEqualTo("/relay/v1/agency/" + delivery.getAgencyId()))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(delivery))));
    // when
    var response = deliveryPublisher.sendSetRiderEvent(delivery);
    // then
    StepVerifier.create(response).expectNext(delivery).verifyComplete();
    WireMock.verify(
        1, postRequestedFor(urlPathEqualTo("/relay/v1/agency/" + delivery.getAgencyId())));
  }

  @Test
  void sendSetRiderEvent_4xx_clientError() throws JsonProcessingException {
    // given
    var delivery = makeValidDelivery();
    stubFor(
        post(urlPathEqualTo("/relay/v1/agency/" + delivery.getAgencyId()))
            .willReturn(aResponse().withStatus(404)));
    // when
    var response = deliveryPublisher.sendSetRiderEvent(delivery);
    // then
    StepVerifier.create(response)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof RelayClientException);
              assertTrue(err.getMessage().contains("Push Event failed for delivery"));
              return true;
            })
        .verify();
    WireMock.verify(
        1, postRequestedFor(urlPathEqualTo("/relay/v1/agency/" + delivery.getAgencyId())));
  }

  @Test
  void sendSetRiderEvent_5xx_serverError() throws JsonProcessingException {
    // given
    var delivery = makeValidDelivery();
    stubFor(
        post(urlPathEqualTo("/relay/v1/agency/" + delivery.getAgencyId()))
            .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    // when
    var response = deliveryPublisher.sendSetRiderEvent(delivery);
    // then
    StepVerifier.create(response)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof RelayServerException);
              assertTrue(err.getMessage().contains("Shop operation failed for delivery :     "));
              return true;
            })
        .verify();
    WireMock.verify(
        1, postRequestedFor(urlPathEqualTo("/relay/v1/agency/" + delivery.getAgencyId())));
  }

  private Delivery makeValidDelivery() {
    return Delivery.builder()
        .id("id-1234")
        .orderId("order1")
        .shopId("shop-1234")
        .riderId("rider-1234")
        .agencyId("agency-1234")
        .customerId("customer-1234")
        .address("서울시 강남구...")
        .phoneNumber("01031583977")
        .deliveryStatus(DeliveryStatus.ACCEPTED)
        .orderTime(LocalDateTime.now().minusMinutes(1))
        .pickupTime(LocalDateTime.now().plusMinutes(30))
        .finishTime(LocalDateTime.now().plusMinutes(60))
        .build();
  }
}
