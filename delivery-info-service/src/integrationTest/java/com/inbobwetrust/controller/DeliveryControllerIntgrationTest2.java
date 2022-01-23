package com.inbobwetrust.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.repository.DeliveryRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeliveryControllerIntgrationTest2 {
    @Autowired
    WebTestClient testClient;
    @SpyBean
    DeliveryRepository deliveryRepository;


    private String proxyShopUrl = "/relay/v1/shop";

    private String proxyAgencyUrl = "/relay/v1/agency";

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    static Delivery makeDeliveryIsPickedUp(DeliveryStatus status) {
        return Delivery.builder()
                .id(LocalDateTime.now().toString())
                .orderId(LocalDateTime.now().toString())
                .customerId("customer-1234")
                .shopId("shop-1234")
                .address("서울시 강남구 삼성동 봉은사로 12-41")
                .deliveryStatus(status)
                .phoneNumber("01031583212")
                .orderTime(LocalDateTime.now())
                .pickupTime(LocalDateTime.now().plusMinutes(1))
                .build();
    }

    @DisplayName("[배달완료 여부 조회API]")
    @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument0={0}, Argument1={1}")
    @MethodSource("isPickedUp_methodSource")
    void isPickedUp_Test(Delivery delivery, boolean isPickedUp) throws JsonProcessingException {
        // Arrange
        deliveryRepository.save(delivery).block(Duration.ofSeconds(1));
        final String testUrl = "/api/delivery/is-picked-up/" + delivery.getId();
        // Act
        var actual =
                testClient
                        .get()
                        .uri(testUrl)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(Boolean.class)
                        .isEqualTo(isPickedUp);
    }

    static Stream<Arguments> isPickedUp_methodSource() {
        return Arrays.stream(DeliveryStatus.values())
                .map(
                        status ->
                                status.equals(DeliveryStatus.PICKED_UP)
                                        ? Arguments.of(makeDeliveryIsPickedUp(status), true)
                                        : Arguments.of(makeDeliveryIsPickedUp(status), false));
    }

    @DisplayName("[주문접수 API]")
    @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument0={0}, Argument1={1}")
    @MethodSource("acceptDelivery_methodSource")
    void acceptDelivery_Test(Delivery delivery, boolean isPickedUp) throws JsonProcessingException {
        // given
        deliveryRepository.save(delivery).block(Duration.ofSeconds(1));
        delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);

        final String testUrl = proxyAgencyUrl + "/" + delivery.getAgencyId();
        stubFor(
                post(urlMatching(proxyAgencyUrl + "/.*"))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(mapper.writeValueAsString(delivery))));

        // when
        var actual =
                testClient
                        .put()
                        .uri("/api/delivery/accept")
                        .bodyValue(delivery)
                        .exchange();

        // then
        if (delivery.getDeliveryStatus().equals(DeliveryStatus.NEW)) {
            actual.expectStatus().isOk().expectBody(Delivery.class).consumeWith(res -> {
                var del = res.getResponseBody();
                deliveryRepository.findById(delivery.getId()).block(Duration.ofSeconds(2)).getDeliveryStatus().equals(DeliveryStatus.ACCEPTED);
                Assertions.assertEquals(DeliveryStatus.ACCEPTED, del.getDeliveryStatus());
            });
        } else {
            actual.expectStatus().isBadRequest().expectBody(String.class).consumeWith(res -> {
                var errMsg = res.getResponseBody();
                Assertions.assertTrue(errMsg.contains("주문상태가 NEW가 아닙니다"));
            });
        }
    }

    static Stream<Arguments> acceptDelivery_methodSource() {
        return Arrays.stream(DeliveryStatus.values())
                .map(
                        status ->
                                status.equals(DeliveryStatus.NEW)
                                        ? Arguments.of(makeDeliveryIsPickedUp(status), true)
                                        : Arguments.of(makeDeliveryIsPickedUp(status), false));

    }
}
