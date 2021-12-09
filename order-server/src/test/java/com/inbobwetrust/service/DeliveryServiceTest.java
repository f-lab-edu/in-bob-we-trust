package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.producer.DeliveryProducer;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
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
    void plusMinutesPerformanceTest() {
        for (int i = 0; i < 1000000; i++) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime later = now.plusMinutes(30);
            Assertions.assertEquals(now.plusMinutes(30), later);
        }
    }
}
