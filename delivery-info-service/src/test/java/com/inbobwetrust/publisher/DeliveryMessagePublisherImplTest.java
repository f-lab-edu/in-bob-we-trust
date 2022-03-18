package com.inbobwetrust.publisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import com.inbobwetrust.domain.Delivery;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DeliveryMessagePublisherImplTest {

  DeliveryMessagePublisherImpl deliveryPublisher;

  AmqpTemplate amqpTemplate;

  @BeforeEach
  void setUp() {
    amqpTemplate = Mockito.mock(AmqpTemplate.class);
    deliveryPublisher = new DeliveryMessagePublisherImpl(amqpTemplate);
  }

  @Test
  void sendAddDeliveryEvent() {
    Mockito.verify(amqpTemplate, times(0))
        .convertAndSend(eq(DeliveryMessagePublisherImpl.shopExchange), any(Delivery.class));
    // given
    var delivery = makeValidDelivery();
    // when
    var stream = deliveryPublisher.sendAddDeliveryEvent(delivery);
    // then
    StepVerifier.create(stream).expectNext(delivery).verifyComplete();
    Mockito.verify(amqpTemplate, times(1))
        .convertAndSend(eq(DeliveryMessagePublisherImpl.shopExchange), any(Delivery.class));
  }

  @Test
  void sendSetRiderEvent() {
    Mockito.verify(amqpTemplate, times(0))
        .convertAndSend(eq(DeliveryMessagePublisherImpl.agencyExchange), any(Delivery.class));
    // given
    var delivery = makeValidDelivery();
    // when
    var stream = deliveryPublisher.sendSetRiderEvent(delivery);
    // then
    StepVerifier.create(stream).expectNext(delivery).verifyComplete();
    Mockito.verify(amqpTemplate, times(1))
        .convertAndSend(eq(DeliveryMessagePublisherImpl.agencyExchange), any(Delivery.class));
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
