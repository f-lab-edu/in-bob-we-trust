package com.inbobwetrust.service;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.publisher.DeliveryPublisher;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceImplTest {
  @InjectMocks DeliveryServiceImpl deliveryService;
  @Mock DeliveryRepository deliveryRepository;
  @Mock DeliveryPublisher deliveryPublisher;

  private Delivery makeValidDelivery() {
    return Delivery.builder()
        .orderId("order-1234")
        .customerId("customer-1234")
        .address("서울시 강남구 삼성동 봉은사로 12-41")
        .phoneNumber("01031583212")
        .orderTime(LocalDateTime.now())
        .build();
  }

  private Delivery makeInvalidDelivery() {
    return Delivery.builder().build();
  }

  @Test
  void addDelivery_success() {
    // Arrange
    Delivery expected = makeValidDelivery();
    // Stub
    when(deliveryRepository.save(isA(Delivery.class))).thenReturn(Mono.just(expected));
    doNothing().when(deliveryPublisher).sendAddDeliveryEvent(isA(Delivery.class));
    // Act
    var result = deliveryService.addDelivery(expected);
    // Assert
    StepVerifier.create(result).expectNext(expected);
    verify(deliveryRepository, times(1)).save(any(Delivery.class));
  }

  @Test
  void addDelivery_fail_null() {
    // Arrange
    // Stub
    when(deliveryRepository.save(any())).thenReturn(Mono.error(IllegalArgumentException::new));
    // Act
    var result = deliveryService.addDelivery(null);
    // Assert
    StepVerifier.create(result).expectError(IllegalArgumentException.class);
    verify(deliveryRepository, times(1)).save(any());
  }
}
