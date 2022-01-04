package com.inbobwetrust.relay;

import com.inbobwetrust.relay.domain.Delivery;
import com.inbobwetrust.relay.domain.DeliveryStatus;
import com.inbobwetrust.relay.domain.ReceiverType;
import com.inbobwetrust.relay.domain.RelayRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@WebFluxTest(RelayController.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class RelayControllerTest {

  @Autowired WebTestClient testClient;

  @MockBean RelayRepository relayRepository;

  private final String BASE_URL = "/relay/v1";

  @DisplayName("[요청전송 to 사장님]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("makeDeliveryReq")
  void sendShopRequest(Delivery delivery) {
    // Arrange
    var expected = new RelayRequest(ReceiverType.SHOP, delivery.getShopId(), delivery);
    var savedRequest = Mono.just(expected);
    var uri = BASE_URL + "/shop/" + delivery.getShopId();
    // Stub
    when(relayRepository.save(any())).thenReturn(savedRequest);
    // Act
    var actual =
        (Delivery) this.makePostRequest_and_expect(HttpStatus.OK, delivery, uri, Delivery.class);
    // Assert
    Assertions.assertEquals(expected.getDelivery(), actual);
  }

  @DisplayName("[요청전송 to 사장님] Delivery 데이터 필수항목 누락")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("makeDeliveryReq")
  void sendShopRequest2(Delivery delivery) {
    // Arrange
    delivery.setId(null);
    var savedRequest =
        Mono.just(new RelayRequest(ReceiverType.SHOP, delivery.getShopId(), delivery));
    var uri = BASE_URL + "/shop/" + delivery.getShopId();
    // Stub
    // Act
    var actual =
        (String)
            this.makePostRequest_and_expect(HttpStatus.BAD_REQUEST, delivery, uri, String.class);
    // Assert
    Assertions.assertTrue(actual.contains("필수"));
  }

  @DisplayName("[요청전송 to 배달대행사]")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("makeDeliveryReq")
  void sendAgencyRequest(Delivery delivery) {
    // Arrange
    var relayRequest = new RelayRequest(ReceiverType.AGENCY, delivery.getAgencyId(), delivery);
    var savedRequest = Mono.just(relayRequest);
    var uri = BASE_URL + "/agency/" + delivery.getAgencyId();
    // Stub
    when(relayRepository.save(any())).thenReturn(savedRequest);
    // Act
    var actual =
        (Delivery) this.makePostRequest_and_expect(HttpStatus.OK, delivery, uri, Delivery.class);
    // Assert
    Assertions.assertEquals(relayRequest.getDelivery(), actual);
  }

  @DisplayName("[요청전송 to 배달대행사] Delivery 데이터 필수항목 누락")
  @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}")
  @MethodSource("makeDeliveryReq")
  void sendAgencyRequest2(Delivery expected) {
    // Arrange
    expected.setId(null);
    var savedRequest =
        Mono.just(new RelayRequest(ReceiverType.SHOP, expected.getShopId(), expected));
    var uri = BASE_URL + "/shop/" + expected.getShopId();
    // Stub
    // Act
    var actual =
        (String)
            this.makePostRequest_and_expect(HttpStatus.BAD_REQUEST, expected, uri, String.class);
    // Assert
    Assertions.assertTrue(actual.contains("필수"));
  }

  private Object makePostRequest_and_expect(
      HttpStatus status, Object body, String uri, Class clazz) {
    return testClient
        .post()
        .uri(uri)
        .bodyValue(body)
        .exchange()
        .expectStatus()
        .isEqualTo(status)
        .expectBody(clazz)
        .returnResult()
        .getResponseBody();
  }

  static Stream<Arguments> makeDeliveryReq() {
    return Stream.of(Arguments.of(makeDelivery()));
  }

  private static Delivery makeDelivery() {
    var delivery = new Delivery();
    delivery.setId("delivery-1234");
    delivery.setShopId("shop-1234");
    delivery.setOrderId("order-1234");
    delivery.setRiderId("rider-1234");
    delivery.setAgencyId("agency-1234");
    delivery.setCustomerId("customer-1234");
    delivery.setAddress("서울시 강남구...");
    delivery.setPhoneNumber("01031583977");
    delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    delivery.setOrderTime(LocalDateTime.now().minusMinutes(1));
    delivery.setPickupTime(LocalDateTime.now().plusMinutes(30));
    delivery.setFinishTime(LocalDateTime.now().plusMinutes(60));
    return delivery;
  }
}
