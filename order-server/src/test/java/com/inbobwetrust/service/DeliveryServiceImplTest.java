package com.inbobwetrust.service;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.publisher.DeliveryPublisher;
import com.inbobwetrust.repository.DeliveryRepository;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    when(deliveryRepository.save(any()))
        .thenReturn(Mono.error(IllegalArgumentException::new)); // Act
    var result = deliveryService.addDelivery(null);
    // Assert
    StepVerifier.create(result).expectError(IllegalArgumentException.class);
    verify(deliveryRepository, times(1)).save(any());
  }

  @Test
  void setDeliveryRider_success() {
    // Arrange
    var expected = makeValidDelivery();
    expected.setId("aererea");
    expected.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    expected.setFinishTime(LocalDateTime.now().plusMinutes(60));
    // Stub
    var output = Mono.just(expected);
    when(deliveryRepository.findById(expected.getId())).thenReturn(output);
    when(deliveryRepository.save(any(Delivery.class))).thenReturn(output);
    // Act
    var result = deliveryService.setDeliveryRider(expected);
    // Assert
    StepVerifier.create(result)
        .consumeNextWith(
            actual -> {
              Assertions.assertEquals(expected, actual);
              verify(deliveryRepository, times(1)).findById(anyString());
              verify(deliveryRepository, times(1)).save(any());
            })
        .verifyComplete();
  }

  @Test
  void setDeliveryRider_fail_invalid_delivery_information() {
    // Arrange
    Delivery expected = makeValidDelivery();
    expected.setId("abcd");
    expected.setRiderId("1234");
    expected.setDeliveryStatus(DeliveryStatus.COMPLETE);
    expected.setFinishTime(null);
    // Stub
    when(deliveryRepository.findById(expected.getId())).thenReturn(Mono.just(expected));
    // Act
    var result = deliveryService.setDeliveryRider(expected);
    // Assert
    StepVerifier.create(result)
        .consumeErrorWith(
            actual -> {
              Assertions.assertTrue(
                  actual.getMessage().contains(DeliveryServiceImpl.MSG_RIDER_ALREADY_SET));
              Assertions.assertTrue(
                  actual
                      .getMessage()
                      .contains(DeliveryServiceImpl.MSG_INVALID_STATUS_FOR_SETRIDER));
              Assertions.assertTrue(
                  actual.getMessage().contains(DeliveryServiceImpl.MSG_NULL_FINISHTIME));
              verify(deliveryRepository, times(1)).findById(anyString());
              verify(deliveryRepository, times(0)).save(any());
            })
        .verify();
  }
}
