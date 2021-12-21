package com.inbobwetrust.service;

import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inbobwetrust.exceptions.NoAffectedRowsSqlException;
import com.inbobwetrust.model.dto.DeliveryStatusDto;
import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.entity.OrderStatus;
import com.inbobwetrust.model.entity.Rider;
import com.inbobwetrust.producer.DeliveryProducer;
import com.inbobwetrust.repository.DeliveryRepository;
import com.inbobwetrust.repository.RiderRepository;
import com.inbobwetrust.util.vo.DeliveryInstanceGenerator;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeliveryServiceTest {
    @InjectMocks DeliveryService deliveryService;

    @Mock DeliveryRepository deliveryRepository;

    @Mock DeliveryProducer deliveryProducer;

    @Mock RiderRepository riderRepository;

    @Test
    @DisplayName("주문상태 픽업완료로 업데이트 : 성공")
    void setStatusToPickup_successTest() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setOrderStatus(OrderStatus.PICKED_UP);
        when(deliveryRepository.update(initialDelivery)).thenReturn(true);
        when(deliveryRepository.findByOrderId(initialDelivery.getOrderId()))
                .thenReturn(Optional.of(initialDelivery));

        Delivery setRiderDelivery = deliveryService.setStatusPickup(initialDelivery);

        assertNotNull(setRiderDelivery.getRiderId());
        verify(deliveryRepository, times(1)).update(any(Delivery.class));
    }

    @Test
    @DisplayName("배달대행사의 라이더배정 성공")
    void setRider_successTest() {
        // SetUp
        Delivery expected = makeDeliveryValid();
        Rider rider =
                Rider.builder().id(expected.getRiderId()).agencyId(expected.getAgencyId()).build();
        // Stub
        when(deliveryRepository.update(expected)).thenReturn(true);
        when(deliveryRepository.findByOrderId(any()))
                .thenReturn(Optional.of(makeDeliveryNullRider()))
                .thenReturn(Optional.of(expected));
        when(riderRepository.findByRiderId(any())).thenReturn(Optional.of(rider));
        // Execute
        Delivery actual = deliveryService.setRider(expected);
        // Assert
        Assertions.assertEquals(expected, actual);
        verify(deliveryRepository, times(1)).update(any(Delivery.class));
        verify(deliveryRepository, times(2)).findByOrderId(any());
        verify(riderRepository, times(1)).findByRiderId(any());
    }

    @Test
    @DisplayName("사장님 주문접수완료 : 성공")
    void addDelivery_successTest() {
        // setup
        Delivery aDelivery = makeDeliveryForRequestAndResponse().get(0);
        aDelivery.setPickupTime(LocalDateTime.now().plusMinutes(30));
        aDelivery.setFinishTime(null);
        aDelivery.setOrderStatus(null);

        // stub
        when(deliveryRepository.save(any())).thenReturn(true);
        when(deliveryRepository.findByOrderId(aDelivery.getOrderId()))
                .thenReturn(Optional.of(aDelivery));

        // execute
        Delivery deliverySaved = deliveryService.addDelivery(aDelivery);
        // assert
        verify(deliveryRepository, times(1)).save(aDelivery);
        verify(deliveryProducer, times(1)).sendAddDeliveryMessage(deliverySaved);
    }

    @Test
    @DisplayName("사장님 주문접수완료 : 주문저장이 안된경우 exception")
    void addDelivery_failTest() {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        deliveryRequest.setFinishTime(null);
        when(deliveryRepository.save(deliveryRequest)).thenReturn(false);

        assertThrows(
                NoAffectedRowsSqlException.class,
                () -> deliveryService.addDelivery(deliveryRequest));

        verify(deliveryRepository, times(1)).save(deliveryRequest);
        verify(deliveryProducer, times(0)).sendAddDeliveryMessage(deliveryRequest);
    }

    @Test
    @DisplayName("사장님 주문접수완료 : 예상도착시간추가")
    void addEstimatedDeliveryFinishTimeTest() {
        Delivery delivery = makeDeliveryForRequestAndResponse().get(0);
        delivery.setFinishTime(null);

        assertNull(delivery.getFinishTime());
        deliveryService.addEstimatedDeliveryFinishTime(delivery);

        assertNotNull(delivery.getFinishTime());
    }

    @Test
    @DisplayName("주문상태 배달완료로 업데이트 : 성공")
    void setStatusToComplete_successTest() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setOrderStatus(OrderStatus.COMPLETE);
        when(deliveryRepository.update(initialDelivery)).thenReturn(true);
        when(deliveryRepository.findByOrderId(initialDelivery.getOrderId()))
                .thenReturn(Optional.of(initialDelivery));

        Delivery setRiderDelivery = deliveryService.setStatusComplete(initialDelivery);

        assertNotNull(setRiderDelivery.getRiderId());
        verify(deliveryRepository, times(1)).update(any(Delivery.class));
    }

    @Test
    @DisplayName("주문상태 배달완료로 업데이트 : Repository에서 save 실패")
    void setStatusToComplete_failTest1() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setOrderStatus(OrderStatus.COMPLETE);

        assertThrows(
                RuntimeException.class, () -> deliveryService.setStatusComplete(initialDelivery));

        verify(deliveryRepository, times(1)).update(any(Delivery.class));
    }

    @Test
    @DisplayName("주문상태확인 service : 성공")
    void findDeliveryStatusByOrderId_success() {
        DeliveryStatusDto expected = new DeliveryStatusDto(1L, OrderStatus.ACCEPTED);
        when(deliveryRepository.findDeliveryStatusByOrderId(expected.getOrderId()))
                .thenReturn(Optional.of(expected));

        DeliveryStatusDto actual =
                deliveryService.findDeliveryStatusByOrderId(expected.getOrderId());

        assertEquals(expected.getOrderStatus(), actual.getOrderStatus());
    }

    @Test
    @DisplayName("[주문접수완료] 실패 : OrderStatus가 new 외의 상태인 경우 에러 발생")
    void addDeliveryTest_fail1() {
        Delivery delivery = makeDeliveryForRequestAndResponse().get(0);

        for (OrderStatus status : OrderStatus.values()) {
            if (!status.equals(OrderStatus.NEW)) {
                delivery.setOrderStatus(status);
                assertThrows(
                        IllegalStateException.class, () -> deliveryService.addDelivery(delivery));
            }
        }
    }

    @Test
    @DisplayName("[주문접수완료] 실패 : (픽업시간==조리완료시간)이 서비스레이어에서 validation 시점의 시간보다 이전이면 오류")
    void addDeliveryTest_fail() {
        Delivery delivery = makeDeliveryForRequestAndResponse().get(0);
        when(deliveryRepository.save(any())).thenReturn(true);
        when(deliveryRepository.findByOrderId(any())).thenReturn(Optional.of(delivery));

        delivery.setOrderStatus(OrderStatus.NEW);
        delivery.setFinishTime(null);
        delivery.setPickupTime(LocalDateTime.now().plusSeconds(10));
        assertDoesNotThrow(() -> deliveryService.addDelivery(delivery));

        delivery.setPickupTime(LocalDateTime.now());
        assertThrows(IllegalStateException.class, () -> deliveryService.addDelivery(delivery));

        delivery.setPickupTime(LocalDateTime.now().minusSeconds(1));
        assertThrows(IllegalStateException.class, () -> deliveryService.addDelivery(delivery));
    }

    @Test
    @DisplayName("[신규주문접수] 성공: 배달예상시간 어플리케이션이 설정")
    void addDeliveryTest_success() {
        when(deliveryRepository.save(any())).thenReturn(true); // stub
        Delivery input = makeDeliveryForRequestAndResponse().get(0); // setup
        input.setPickupTime(LocalDateTime.now().plusMinutes(30));
        input.setFinishTime(null);

        //
        assertNull(input.getFinishTime()); // execute
        Delivery output = deliveryService.addDelivery(input);

        //
        assertNotNull(output.getFinishTime()); // assertions
        verify(deliveryRepository, times(1)).save(input);
        verify(deliveryProducer, times(1)).sendAddDeliveryMessage(output);
    }

    @Test
    @DisplayName("[신규주문접수] 실패 : 배달예상시간 사전에 미리 설정되어있음")
    void addDeliveryTest_fail2() {
        when(deliveryRepository.save(any())).thenReturn(true); // stub
        Delivery inputFail = makeDeliveryForRequestAndResponse().get(0);
        inputFail.setFinishTime(LocalDateTime.now().plusMinutes(60));
        // execute
        assertThrows(IllegalStateException.class, () -> deliveryService.addDelivery(inputFail));
        // assert
        verify(deliveryRepository, times(0)).save(any());
        verify(deliveryProducer, times(0)).sendAddDeliveryMessage(any());
    }

    @Test
    @DisplayName("[라이더배정] 실패 : 미존재 주문번호, UPDATE 아무것도 발생하지 않음")
    void setRiderTest_fail2() {
        // SetupData
        Delivery aDelivery = makeDeliveryForRequestAndResponse().get(0);
        aDelivery.setId(-1L);
        aDelivery.setFinishTime(LocalDateTime.now().plusMinutes(60));
        aDelivery.setOrderStatus(OrderStatus.ACCEPTED);
        // Stub
        when(riderRepository.findByRiderId(any()))
                .thenReturn(Optional.of(makeExpectedRider(aDelivery)));
        when(deliveryRepository.update(any())).thenReturn(false);
        when(deliveryRepository.findByOrderId(any()))
                .thenReturn(Optional.of(makeDeliveryNullRider()))
                .thenReturn(Optional.of(aDelivery));
        // Execute
        assertThrows(NoAffectedRowsSqlException.class, () -> deliveryService.setRider(aDelivery));
        // Assert
        verify(deliveryRepository, times(1)).update(any());
    }

    private Rider makeExpectedRider(Delivery delivery) {
        return Rider.builder().id(delivery.getRiderId()).agencyId(delivery.getAgencyId()).build();
    }

    @Test
    @DisplayName("[라이더배정] 성공 : 존재 주문번호, UPDATE 발생")
    void setRiderTest_fail4() {
        // SetUpData
        Delivery deliveryNullRider = makeDeliveryNullRider();
        Delivery expected = makeDeliveryValid();
        // Stub
        when(riderRepository.findByRiderId(any()))
                .thenReturn(Optional.of(makeExpectedRider(expected)));
        when(deliveryRepository.update(any())).thenReturn(true);
        when(deliveryRepository.findByOrderId(any()))
                .thenReturn(Optional.of(deliveryNullRider))
                .thenReturn(Optional.of(expected));
        // Execute
        Delivery actual = deliveryService.setRider(expected);
        // Assert
        assertEquals(expected, actual);
        verify(deliveryRepository, times(1)).update(any());
    }

    private Delivery makeDeliveryValid() {
        Delivery aDelivery = makeDeliveryForRequestAndResponse().get(0);
        aDelivery.setFinishTime(LocalDateTime.now().plusMinutes(60));
        aDelivery.setOrderStatus(OrderStatus.ACCEPTED);
        return aDelivery;
    }

    private Delivery makeDeliveryNullRider() {
        Delivery delivery = makeDeliveryForRequestAndResponse().get(0);
        delivery.setRiderId(null);
        return delivery;
    }

    @Test
    @DisplayName("[라이더배정] 실패 : 배달완료시간 미설정 UPDATE 발생")
    void setRiderTest_fail5() {
        // SetUp
        Delivery aDelivery = makeDeliveryForRequestAndResponse().get(0);
        aDelivery.setFinishTime(LocalDateTime.now().plusMinutes(60));
        aDelivery.setOrderStatus(OrderStatus.ACCEPTED);
        aDelivery.setFinishTime(null);
        // Exexcute
        assertThrows(IllegalStateException.class, () -> deliveryService.setRider(aDelivery));
        // Assert
        verify(riderRepository, times(0)).findByRiderId(any());
        verify(deliveryRepository, times(0)).update(any());
        verify(deliveryRepository, times(0)).findByOrderId(any());
    }

    @Test
    @DisplayName("[라이더배정] 실패 : 이미 라이더 지정")
    void setRiderTest_fail_alreadySetRider() {
        // Setup (data)
        Delivery aDelivery = makeDeliveryForRequestAndResponse().get(0);
        aDelivery.setFinishTime(LocalDateTime.now().plusMinutes(60));
        aDelivery.setOrderStatus(OrderStatus.ACCEPTED);
        aDelivery.setRiderId(1L);
        // Stub
        when(riderRepository.findByRiderId(any()))
                .thenReturn(Optional.of(makeExpectedRider(aDelivery)));
        when(deliveryRepository.findByOrderId(any())).thenReturn(Optional.of(aDelivery));
        // Execute
        assertThrows(IllegalStateException.class, () -> deliveryService.setRider(aDelivery));
        // Assert
        verify(riderRepository, times(1)).findByRiderId(any());
        verify(deliveryRepository, times(1)).findByOrderId(any());
        verify(deliveryRepository, times(0)).update(any());
    }
}
