package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.producer.DeliveryProducer;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    @DisplayName("배달대행사의 라이더배정 성공")
    void setRider_successTest() {
        LocalDateTime now = LocalDateTime.now();
        Delivery initialDelivery =
                Delivery.builder()
                        .orderId("order-1")
                        .riderId("rider-1")
                        .wantedPickupTime(now.plusMinutes(30))
                        .estimatedDeliveryFinishTime(now.plusMinutes(60))
                        .deliveryAgentId("agent-1")
                        .build();
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
        LocalDateTime now = LocalDateTime.now();
        Delivery initialDelivery =
                Delivery.builder()
                        .orderId("order-1")
                        .deliveryAgentId("agent-1")
                        .wantedPickupTime(now.plusMinutes(30))
                        .estimatedDeliveryFinishTime(now.plusMinutes(60))
                        .build();

        assertThrows(RuntimeException.class, () -> deliveryService.setRider(initialDelivery));

        verify(deliveryRepository, times(0)).update(any(Delivery.class));
        verify(deliveryProducer, times(0)).sendSetRiderMessage(any(Delivery.class));
    }

    @Test
    @DisplayName("배달대행사의 라이더배정 실패 : 배달대행사 누락")
    void setRider_failTest2() {
        LocalDateTime now = LocalDateTime.now();
        Delivery initialDelivery =
                Delivery.builder()
                        .orderId("order-1")
                        .riderId("rider-1")
                        .wantedPickupTime(now.plusMinutes(30))
                        .estimatedDeliveryFinishTime(now.plusMinutes(60))
                        .build();

        assertThrows(
                IllegalArgumentException.class, () -> deliveryService.setRider(initialDelivery));

        verify(deliveryRepository, times(0)).update(any(Delivery.class));
        verify(deliveryProducer, times(0)).sendSetRiderMessage(any(Delivery.class));
    }
}
