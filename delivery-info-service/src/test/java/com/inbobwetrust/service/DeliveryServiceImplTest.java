package com.inbobwetrust.service;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.DeliveryNotFoundException;
import com.inbobwetrust.exception.RetryExhaustedException;
import com.inbobwetrust.publisher.DeliveryPublisher;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
              assertTrue(actual.getMessage().contains(DeliveryServiceImpl.MSG_RIDER_ALREADY_SET));
              assertTrue(
                  actual
                      .getMessage()
                      .contains(DeliveryServiceImpl.MSG_INVALID_STATUS_FOR_SETRIDER));
              assertTrue(actual.getMessage().contains(DeliveryServiceImpl.MSG_NULL_FINISHTIME));
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

  //  @Test
  //  void setPickUp_fail_invalid_statusChange() {
  //    // Arrange
  //    var beforeData = makeValidSetPickUpDelivery();
  //    beforeData.setDeliveryStatus(DeliveryStatus.COMPLETE);
  //    var afterData = makeValidSetPickUpDelivery();
  //    afterData.setDeliveryStatus(DeliveryStatus.PICKED_UP);
  //    // Stub
  //
  // when(primaryDeliveryRepository.findById(beforeData.getId())).thenReturn(Mono.just(beforeData));
  //    // Act
  //    var stream = deliveryService.setPickedUp(afterData);
  //    // Assert
  //    StepVerifier.create(stream)
  //        .expectErrorMatches(
  //            err -> {
  //              Assertions.assertEquals(IllegalArgumentException.class, err.getClass()}
  //        )
  //            })
  //        .verify();
  //    verify(primaryDeliveryRepository, times(1)).findById(anyString());
  //    verify(primaryDeliveryRepository, times(0)).save(any(Delivery.class));
  //  }

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
    var expectedErrorMessage = "배달완료로 전환이 불가한 상태입니다.     기존주문상태: ACCEPTED       요청한주문상태: PICKED_UP";
    var beforeData = makeValidSetPickUpDelivery();
    beforeData.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    var afterData = makeValidSetPickUpDelivery();
    afterData.setDeliveryStatus(DeliveryStatus.PICKED_UP);
    // Stub
    when(deliveryRepository.findById(beforeData.getId())).thenReturn(Mono.just(beforeData));
    // Act
    var stream = deliveryService.setComplete(afterData);
    // Assert
    StepVerifier.create(stream)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof IllegalStateException);
              assertTrue(err.getMessage().equals(expectedErrorMessage));
              return true;
            })
        .verify();
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

  @Test
  @DisplayName("주문상태가 null 일경우 IllegalStateException 발생")
  void acceptDeliveryTest() {
    // given
    var delivery = makeValidDelivery();
    delivery.setDeliveryStatus(null);
    // when
    var resultStream = deliveryService.acceptDelivery(delivery);
    // then
    StepVerifier.create(resultStream)
        .consumeErrorWith(
            err -> {
              assertTrue(err instanceof IllegalStateException);
              assertTrue(err.getMessage().contains("주문상태가 null 입니다."));
            })
        .verify();
  }

  @Test
  @DisplayName(
      "[DeliveryServiceImpl] TImeoutException의 retry 를 전부 실패하고 RetryExhaustedException 발생시")
  void addDeliveryTest_retryExhausted() {
    // given
    var delivery = makeValidDelivery();
    // stub
    when(deliveryRepository.save(any(Delivery.class)))
        .thenReturn(
            Mono.just(delivery)
                .delayElement(
                    DeliveryService.FIXED_DELAY.plusMillis(10))); // 지정된 타임아웃시간보다 0.01s 길게 지연시키기
    // when
    var resultStream = deliveryService.addDelivery(delivery);
    // then
    StepVerifier.create(resultStream)
        .expectSubscription()
        .expectNoEvent(DeliveryService.FIXED_DELAY.multipliedBy(DeliveryService.MAX_ATTEMPTS + 1))
        .expectError(RetryExhaustedException.class)
        .verify();
    verify(deliveryRepository, times(1)).save(any());
    verify(deliveryPublisher, times(0)).sendAddDeliveryEvent(any());
  }

  @Test
  void monoDelayTest() {
    var res = makeInvalidDelivery();
    var stream = Mono.just(res).delayElement(DeliveryService.FIXED_DELAY.plusMillis(1));

    StepVerifier.create(stream).expectNoEvent(DeliveryService.FIXED_DELAY).expectNext(res);
  }

  @Test
  void accpetDelivery_fail_StatusIsNotAccepted() {
    // given
    var delivery = makeValidDelivery();
    delivery.setDeliveryStatus(DeliveryStatus.COMPLETE);
    // when
    var stream = deliveryService.acceptDelivery(delivery);
    // then
    StepVerifier.create(stream)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof IllegalStateException);
              assertTrue(err.getMessage().contains("주문상태가 ACCEPTED가 아닙니다"));
              return true;
            })
        .verify();
    verify(deliveryRepository, times(0)).save(any());
    verify(deliveryRepository, times(0)).findById(anyString());
    verify(deliveryPublisher, times(0)).sendSetRiderEvent(any());
  }

  @Test
  void accpetDelivery_success() {
    // given
    var delivery = makeValidDelivery();
    delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    delivery.setPickupTime(delivery.getOrderTime().plusSeconds(1));
    // stub
    when(deliveryRepository.findById(delivery.getId())).thenReturn(Mono.just(delivery));
    when(deliveryRepository.save(any())).thenReturn(Mono.just(delivery));
    when(deliveryPublisher.sendSetRiderEvent(any())).thenReturn(Mono.just(delivery));
    // when
    var stream = deliveryService.acceptDelivery(delivery);
    // then
    StepVerifier.create(stream).expectNext(delivery).verifyComplete();
    verify(deliveryRepository, times(1)).save(any());
    verify(deliveryRepository, times(1)).findById(delivery.getId());
    verify(deliveryPublisher, times(1)).sendSetRiderEvent(any());
  }

  @Test
  @DisplayName("FAIL pickupTime을 orderTime 보다 1초 일찍(빠르게변경)")
  void acceptDeliveryPickupTImeAfterOrderTime_failTest() {
    // given
    var delivery = makeValidDelivery();
    delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    delivery.setPickupTime(delivery.getOrderTime().minusSeconds(1));
    // when
    var stream = deliveryService.acceptDelivery(delivery);
    // then
    StepVerifier.create(stream)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof IllegalStateException);
              assertTrue(err.getMessage().contains("픽업시간은 주문시간 이후여야 합니다."));
              return true;
            })
        .verify();
    verify(deliveryRepository, times(0)).save(any());
    verify(deliveryRepository, times(0)).findById(anyString());
    verify(deliveryPublisher, times(0)).sendSetRiderEvent(any());
  }

  @Test
  void canSetPickUpTest() {
    // given
    var existingDelivery = makeValidDelivery();
    existingDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    existingDelivery.setPickupTime(existingDelivery.getOrderTime().plusSeconds(1));
    var newDelivery = makeValidDelivery();
    newDelivery.setDeliveryStatus(DeliveryStatus.PICKED_UP);
    newDelivery.setPickupTime(newDelivery.getOrderTime().plusSeconds(1));
    // stub
    when(deliveryRepository.findById(existingDelivery.getId()))
        .thenReturn(Mono.just(existingDelivery));
    when(deliveryRepository.save(any(Delivery.class))).thenReturn(Mono.just(newDelivery));
    // when
    var stream = deliveryService.setPickedUp(newDelivery);
    // then
    StepVerifier.create(stream).expectNext(newDelivery).verifyComplete();
  }

  @Test
  void canSetPickUpTest_fail() {
    // given
    var existingDelivery = makeValidDelivery();
    existingDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
    existingDelivery.setPickupTime(existingDelivery.getOrderTime().plusSeconds(1));
    var newDelivery = makeValidDelivery();
    newDelivery.setDeliveryStatus(DeliveryStatus.COMPLETE);
    newDelivery.setPickupTime(newDelivery.getOrderTime().plusSeconds(1));
    // stub
    when(deliveryRepository.findById(existingDelivery.getId()))
        .thenReturn(Mono.just(existingDelivery));
    // when
    var stream = deliveryService.setPickedUp(newDelivery);
    // then
    StepVerifier.create(stream)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof IllegalStateException);
              assertTrue(err.getMessage().contains("픽업완료로 전환이 불가한 상태입니다."));
              return true;
            })
        .verify();
  }

  @Test
  void canSetPickUp_failTest() {
    // given
    var existingDelivery = makeValidDelivery();
    existingDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED.getNext());
    existingDelivery.setPickupTime(existingDelivery.getOrderTime().plusSeconds(1));
    var newDelivery = makeValidDelivery();
    newDelivery.setDeliveryStatus(DeliveryStatus.PICKED_UP);
    newDelivery.setPickupTime(newDelivery.getOrderTime().plusSeconds(1));
    // stub
    when(deliveryRepository.findById(existingDelivery.getId()))
        .thenReturn(Mono.just(existingDelivery));
    // when
    var stream = deliveryService.setPickedUp(newDelivery);
    // then
    StepVerifier.create(stream)
        .expectErrorMatches(
            err -> {
              assertTrue(err instanceof IllegalStateException);
              assertTrue(err.getMessage().contains("픽업완료로 전환이 불가한 상태입니다.     기존주문상태:"));
              return true;
            })
        .verify();
  }

  @Test
  void canSetCompleteTest() {
    // given
    var existingDelivery = makeValidDelivery();
    existingDelivery.setDeliveryStatus(DeliveryStatus.PICKED_UP);
    existingDelivery.setPickupTime(existingDelivery.getOrderTime().plusSeconds(1));
    var newDelivery = makeValidDelivery();
    newDelivery.setDeliveryStatus(DeliveryStatus.COMPLETE);
    newDelivery.setPickupTime(newDelivery.getOrderTime().plusSeconds(1));
    // stub
    when(deliveryRepository.findById(existingDelivery.getId()))
        .thenReturn(Mono.just(existingDelivery));
    when(deliveryRepository.save(any(Delivery.class))).thenReturn(Mono.just(newDelivery));
    // when
    var stream = deliveryService.setComplete(newDelivery);
    // then
    StepVerifier.create(stream).expectNext(newDelivery).verifyComplete();
    verify(deliveryRepository, times(1)).save(any());
    verify(deliveryRepository, times(1)).findById(existingDelivery.getId());
  }
}
