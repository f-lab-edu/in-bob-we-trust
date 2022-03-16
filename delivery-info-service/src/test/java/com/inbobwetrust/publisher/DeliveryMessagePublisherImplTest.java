package com.inbobwetrust.publisher;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

import com.inbobwetrust.domain.Delivery;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DeliveryMessagePublisherImplTest {

  @InjectMocks DeliveryMessagePublisherImpl deliveryPublisher;

  @Mock AmqpTemplate amqpTemplate;

  private final String SHOP_EXCHANGE = "shop-exchange";

  private final String AGENCY_EXCHANGE = "agency-exchange";

  @BeforeEach
  void setUp() {
    deliveryPublisher.setShopExchange("shop-exchange");
    deliveryPublisher.setAgencyExchange("agency-exchange");
  }

  @Test
  void sendAddDeliveryEvent() {
    Mockito.verify(amqpTemplate, times(0)).convertAndSend(eq(SHOP_EXCHANGE), any(Delivery.class));
    // given
    var delivery = makeValidDelivery();
    // when
    var stream = deliveryPublisher.sendAddDeliveryEvent(delivery);
    // then
    StepVerifier.create(stream).expectNext(delivery).verifyComplete();
    Mockito.verify(amqpTemplate, times(1)).convertAndSend(eq(SHOP_EXCHANGE), any(Delivery.class));
  }

  @Test
  void sendSetRiderEvent() {
    Mockito.verify(amqpTemplate, times(0)).convertAndSend(eq(AGENCY_EXCHANGE), any(Delivery.class));
    // given
    var delivery = makeValidDelivery();
    // when
    var stream = deliveryPublisher.sendSetRiderEvent(delivery);
    // then
    StepVerifier.create(stream).expectNext(delivery).verifyComplete();
    Mockito.verify(amqpTemplate, times(1)).convertAndSend(eq(AGENCY_EXCHANGE), any(Delivery.class));
  }

  private Delivery makeValidDelivery() {
    return Delivery.builder()
        .shopId("shop1234")
        .orderId("order-1234")
        .customerId("customer-1234")
        .address("서울시 강남구 삼성동 봉은사로 12-41")
        .phoneNumber("01031583212")
        .orderTime(LocalDateTime.now())
        .build();
  }
}
