package com.inbobwetrust.model.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryTest {

    @Test
    void deepCopyTest() {
        Delivery original =
                Delivery.builder()
                        .id(1L)
                        .orderId(1L)
                        .riderId(1L)
                        .agencyId(1L)
                        .orderStatus(OrderStatus.COMPLETE)
                        .pickupTime(LocalDateTime.now())
                        .finishTime(LocalDateTime.now().plusMinutes(30))
                        .build();
        Delivery copy = original.deepCopy();

        assertTrue(copy.equals(original) && copy != original);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getOrderId(), copy.getOrderId());
        assertEquals(original.getRiderId(), copy.getRiderId());
        assertEquals(original.getAgencyId(), copy.getAgencyId());
        assertEquals(original.getOrderStatus(), copy.getOrderStatus());
        assertEquals(original.getPickupTime(), copy.getPickupTime());
        assertEquals(original.getFinishTime(), copy.getFinishTime());
        assertEquals(original.getCreatedAt(), copy.getCreatedAt());
        assertEquals(original.getUpdatedAt(), copy.getUpdatedAt());
    }
}
