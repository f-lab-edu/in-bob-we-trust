package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.repository.DeliveryRepository;
import com.inbobwetrust.util.vo.DeliveryInstanceGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {
    @InjectMocks DeliveryService deliveryService;

    @Mock DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("주문상태 배달완료로 업데이트 : 성공")
    void setStatusToComplete_successTest() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setStatus("complete");
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
        initialDelivery.setStatus("complete");

        assertThrows(
                RuntimeException.class, () -> deliveryService.setStatusComplete(initialDelivery));

        verify(deliveryRepository, times(1)).update(any(Delivery.class));
    }

    @Test
    @DisplayName("주문상태 배달완료로 업데이트 : 값이 없어서 실패")
    void setStatusToComplete_failTest2() {
        Delivery initialDelivery = DeliveryInstanceGenerator.makeSimpleNumberedDelivery(1);
        initialDelivery.setStatus(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> deliveryService.setStatusComplete(initialDelivery));

        verify(deliveryRepository, times(0)).update(any(Delivery.class));
    }
}
