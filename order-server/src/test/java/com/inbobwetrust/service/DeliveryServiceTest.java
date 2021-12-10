package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @InjectMocks DeliveryService deliveryService;

    @Mock DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("주문상태확인 service : 성공")
    void findDeliveryStatusByOrderId_success() {
        DeliveryStatus expected = new DeliveryStatus("order-1", "cooking");
        when(deliveryRepository.findDeliveryStatusByOrderId(expected.getOrderId()))
                .thenReturn(Optional.of(expected));

        DeliveryStatus actual = deliveryService.findDeliveryStatusByOrderId(expected.getOrderId());

        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @DisplayName("주문상태확인 service : 실패, 존재하지 않는 orderId")
    void findDeliveryStatusByOrderId_fail() {
        DeliveryStatus expected = new DeliveryStatus("order-1", "cooking");
        when(deliveryRepository.findDeliveryStatusByOrderId(expected.getOrderId()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(
                RuntimeException.class,
                () -> deliveryService.findDeliveryStatusByOrderId(expected.getOrderId()));
    }
}
