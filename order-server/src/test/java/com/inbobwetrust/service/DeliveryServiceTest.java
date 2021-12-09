package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.producer.DeliveryProducer;
import com.inbobwetrust.repository.DeliveryRepository;
import com.inbobwetrust.util.vo.DeliveryInstanceGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {
    @InjectMocks DeliveryService deliveryService;

    @Mock DeliveryRepository deliveryRepository;

    @Mock DeliveryProducer deliveryProducer;

    @Test
    @DisplayName("주문생성완료 : 성공")
    void addDelivery_successTest() {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        Delivery expectedDeliveryResponse = makeDeliveryForRequestAndResponse().get(1);
        when(deliveryRepository.save(deliveryRequest)).thenReturn(true);
        when(deliveryRepository.findByOrderId(deliveryRequest.getOrderId()))
                .thenReturn(Optional.of(expectedDeliveryResponse));

        Delivery deliverySaved = deliveryService.addDelivery(deliveryRequest);

        verify(deliveryRepository, times(1)).save(deliveryRequest);
        verify(deliveryProducer, times(1)).sendAddDeliveryMessage(deliverySaved);
    }

    @Test
    @DisplayName("주문생성완료 : 주문저장이 안된경우 exception")
    void addDelivery_failTest() {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        Delivery expectedDeliveryResponse = makeDeliveryForRequestAndResponse().get(1);
        when(deliveryRepository.save(deliveryRequest)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> deliveryService.addDelivery(deliveryRequest));

        verify(deliveryRepository, times(1)).save(deliveryRequest);
        verify(deliveryProducer, times(0)).sendAddDeliveryMessage(deliveryRequest);
    }

    @Test
    @DisplayName("예상도착시간추가 픽업요청시간에 30분 고정적인 예상배달시간")
    void addEstimatedDeliveryFinishTimeTest() {
        Delivery delivery = makeDeliveryForRequestAndResponse().get(0);
        assertNull(delivery.getEstimatedDeliveryFinishTime());

        deliveryService.addEstimatedDeliveryFinishTime(delivery);

        assertEquals(
                delivery.getWantedPickupTime().plusMinutes(30),
                delivery.getEstimatedDeliveryFinishTime());
    }

    @Test
    @DisplayName("배달대행사의 라이더배정 성공")
    void setRider_successTest() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setRiderId("rider-1");

        when(deliveryRepository.update(initialDelivery)).thenReturn(true);
        when(deliveryRepository.findByOrderId(initialDelivery.getOrderId()))
                .thenReturn(Optional.of(initialDelivery));

        Delivery setRiderDelivery = deliveryService.setRider(initialDelivery);

        assertNotNull(setRiderDelivery.getRiderId());
        verify(deliveryRepository, times(1)).update(any(Delivery.class));
        verify(deliveryProducer, times(1)).sendSetRiderMessage(any(Delivery.class));
    }

    @Test
    @DisplayName("배달대행사의 라이더배정 실패 : 라이더 아이디 누락")
    void setRider_failTest() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setRiderId(null);

        assertThrows(RuntimeException.class, () -> deliveryService.setRider(initialDelivery));

        verify(deliveryRepository, times(0)).update(any(Delivery.class));
        verify(deliveryProducer, times(0)).sendSetRiderMessage(any(Delivery.class));
    }

    @Test
    @DisplayName("배달대행사의 라이더배정 실패 : 배달대행사 누락")
    void setRider_failTest2() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setDeliveryAgentId(null);

        assertThrows(
                IllegalArgumentException.class, () -> deliveryService.setRider(initialDelivery));

        verify(deliveryRepository, times(0)).update(any(Delivery.class));
        verify(deliveryProducer, times(0)).sendSetRiderMessage(any(Delivery.class));
    }

    @Test
    @DisplayName("주문상태 픽업완료로 업데이트 : 성공")
    void setStatusToPickup_successTest() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setStatus("picked up");
        when(deliveryRepository.update(initialDelivery)).thenReturn(true);
        when(deliveryRepository.findByOrderId(initialDelivery.getOrderId()))
                .thenReturn(Optional.of(initialDelivery));

        Delivery setRiderDelivery = deliveryService.updateDeliveryStatusPickup(initialDelivery);

        assertNotNull(setRiderDelivery.getRiderId());
        verify(deliveryRepository, times(1)).update(any(Delivery.class));
        verify(deliveryProducer, times(1)).sendSetStatusPickupMessage(any(Delivery.class));
    }

    @Test
    @DisplayName("주문상태 픽업완료로 업데이트 : 실패")
    void setStatusToPickup_failTest() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setStatus(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> deliveryService.updateDeliveryStatusPickup(initialDelivery));

        verify(deliveryRepository, times(0)).update(any(Delivery.class));
        verify(deliveryProducer, times(0)).sendSetRiderMessage(any(Delivery.class));
    }

    @Test
    @DisplayName("주문상태 배달완료로 업데이트 : 성공")
    void setStatusToComplete_successTest() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setStatus("complete");
        when(deliveryRepository.update(initialDelivery)).thenReturn(true);
        when(deliveryRepository.findByOrderId(initialDelivery.getOrderId()))
                .thenReturn(Optional.of(initialDelivery));

        Delivery setRiderDelivery = deliveryService.updateDeliveryStatusComplete(initialDelivery);

        assertNotNull(setRiderDelivery.getRiderId());
        verify(deliveryRepository, times(1)).update(any(Delivery.class));
        verify(deliveryProducer, times(1)).sendSetStatusCompleteMessage(any(Delivery.class));
    }

    @Test
    @DisplayName("주문상태 배달완료로 업데이트 : Repository에서 save 실패")
    void setStatusToComplete_failTest1() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setStatus("complete");

        assertThrows(
                RuntimeException.class,
                () -> deliveryService.updateDeliveryStatusPickup(initialDelivery));

        verify(deliveryRepository, times(1)).update(any(Delivery.class));
        verify(deliveryProducer, times(0)).sendSetRiderMessage(any(Delivery.class));
    }

    @Test
    @DisplayName("주문상태 배달완료로 업데이트 : 값이 없어서 실패")
    void setStatusToComplete_failTest2() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setStatus(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> deliveryService.updateDeliveryStatusPickup(initialDelivery));

        verify(deliveryRepository, times(0)).update(any(Delivery.class));
        verify(deliveryProducer, times(0)).sendSetRiderMessage(any(Delivery.class));
    }
}
