package com.inbobwetrust.domain;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.inbobwetrust.repository.DeliveryRepositoryImpl;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.test.StepVerifier;

@AutoConfigureWireMock(port = 0)
@ExtendWith(MockitoExtension.class)
class DeliveryRepositoryImplTest {

  @InjectMocks DeliveryRepositoryImpl deliveryRepository;

  private WireMockServer wireMockServer;

  private ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    wireMockServer.start();
    deliveryRepository.setUriDeliveryInfoService("http://127.0.0.1:" + wireMockServer.port());
  }

  static Stream<Arguments> isPickedUpParameters() {
    return Stream.of(Arguments.of(true), Arguments.of(false));
  }

  @ParameterizedTest
  @MethodSource("isPickedUpParameters")
  void isPickedUp_test(Boolean isPickedUp) throws JsonProcessingException {
    // given
    var deliveryId = "delivery-123412312973012feahl";
    // when
    wireMockServer.stubFor(
        get(urlMatching("/is-picked-up/" + deliveryId))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mapper.writeValueAsString(isPickedUp))
                    .withStatus(HttpStatus.OK.value())));
    // then
    var resultStream = deliveryRepository.isPickedUp(deliveryId);
    StepVerifier.create(resultStream).expectNext(isPickedUp).verifyComplete();
  }
}
