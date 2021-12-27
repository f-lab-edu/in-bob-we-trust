package com.inbobwetrust.service;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.DeliveryNotFoundException;
import com.inbobwetrust.publisher.DeliveryPublisher;
import com.inbobwetrust.repository.DeliveryRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
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

  @Test
  void setPickUp_success() {
    // Arrange
    var beforeData = makeValidSetPickUpDelivery();
    beforeData.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    var afterData = makeValidSetPickUpDelivery();
    afterData.setDeliveryStatus(DeliveryStatus.PICKED_UP);
    // Stub
    when(deliveryRepository.findById(beforeData.getId())).thenReturn(Mono.just(beforeData));
    when(deliveryRepository.save(isA(Delivery.class))).thenReturn(Mono.just(afterData));
    // Act
    var stream = deliveryService.setPickedUp(afterData);
    // Assert
    StepVerifier.create(stream).expectNext(afterData).verifyComplete();
    verify(deliveryRepository, times(1)).findById(anyString());
    verify(deliveryRepository, times(1)).save(any(Delivery.class));
  }

  @Test
  void setPickUp_fail_invalid_statusChange() {
    // Arrange
    var beforeData = makeValidSetPickUpDelivery();
    beforeData.setDeliveryStatus(DeliveryStatus.COMPLETE);
    var afterData = makeValidSetPickUpDelivery();
    afterData.setDeliveryStatus(DeliveryStatus.PICKED_UP);
    // Stub
    when(deliveryRepository.findById(beforeData.getId())).thenReturn(Mono.just(beforeData));
    // Act
    var stream = deliveryService.setPickedUp(afterData);
    // Assert
    StepVerifier.create(stream)
        .consumeErrorWith(
            err -> Assertions.assertEquals(IllegalArgumentException.class, err.getClass()))
        .verify();
    verify(deliveryRepository, times(1)).findById(anyString());
    verify(deliveryRepository, times(0)).save(any(Delivery.class));
  }

  private Delivery makeValidSetPickUpDelivery() {
    return Delivery.builder()
        .id("id-1234")
        .orderId("order1")
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

  @Test
  void setComplete_success() {
    // Arrange
    var beforeData = makeValidSetPickUpDelivery();
    beforeData.setDeliveryStatus(DeliveryStatus.PICKED_UP);
    var afterData = makeValidSetPickUpDelivery();
    afterData.setDeliveryStatus(DeliveryStatus.COMPLETE);
    // Stub
    when(deliveryRepository.findById(beforeData.getId())).thenReturn(Mono.just(beforeData));
    when(deliveryRepository.save(isA(Delivery.class))).thenReturn(Mono.just(afterData));
    // Act
    var stream = deliveryService.setComplete(afterData);
    // Assert
    StepVerifier.create(stream).expectNext(afterData).verifyComplete();
    verify(deliveryRepository, times(1)).findById(anyString());
    verify(deliveryRepository, times(1)).save(any(Delivery.class));
  }

  @Test
  void setComplete_fail_invalid_status() {
    // Arrange
    var beforeData = makeValidSetPickUpDelivery();
    beforeData.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    var afterData = makeValidSetPickUpDelivery();
    afterData.setDeliveryStatus(DeliveryStatus.PICKED_UP);
    // Stub
    when(deliveryRepository.findById(beforeData.getId())).thenReturn(Mono.just(beforeData));
    // Act
    var stream = deliveryService.setComplete(afterData);
    // Assert
    StepVerifier.create(stream).expectError(IllegalArgumentException.class).verify();
    verify(deliveryRepository, times(1)).findById(anyString());
    verify(deliveryRepository, times(0)).save(any(Delivery.class));
  }

  @Test
  void findById_success() {
    // Arrange
    var delivery = makeValidDelivery();
    // Stub
    when(deliveryRepository.findById(delivery.getId())).thenReturn(Mono.just(delivery));
    // Act
    var resultStream = deliveryService.findById(delivery.getId());
    // Assert
    StepVerifier.create(resultStream).expectNext(delivery);
  }

  @Test
  void findById_fail_doesNotExist() {
    // Arrange
    var delivery = makeValidDelivery();
    // Stub
    when(deliveryRepository.findById(delivery.getId())).thenReturn(Mono.empty());
    // Act
    var resultStream = deliveryService.findById(delivery.getId());
    // Assert
    StepVerifier.create(resultStream).expectError(DeliveryNotFoundException.class).verify();
  }

  @Test
  void findAll_success() {
    // Arrange
    var delivery = makeValidDelivery();
    var pageable = PageRequest.of(0, 10);
    // Stub
    when(deliveryRepository.findAllByOrderIdContaining(anyString(), any(Pageable.class)))
        .thenReturn(Flux.fromIterable(makeValidDeliveries(10)));
    // Act
    var resultStream = deliveryService.findAll(pageable);
    // Assert
    StepVerifier.create(resultStream).expectNextCount(10).verifyComplete();
  }

  @Test
  void findAll_fail() {
    // Arrange
    var delivery = makeValidDelivery();
    var pageable = PageRequest.of(0, 10);
    // Stub
    when(deliveryRepository.findAllByOrderIdContaining(anyString(), any(Pageable.class)))
        .thenReturn(Flux.empty());
    // Act
    var resultStream = deliveryService.findAll(pageable);
    // Assert
    StepVerifier.create(resultStream).expectError(DeliveryNotFoundException.class).verify();
  }

  private List<Delivery> makeValidDeliveries(int count) {
    var deliveries = new ArrayList<Delivery>();
    for (int i = 1; i <= count; i++) {
      var dlvry = makeValidDelivery();
      dlvry.setOrderId("order-" + i);
      deliveries.add(dlvry);
    }
    return deliveries;
  }
}
