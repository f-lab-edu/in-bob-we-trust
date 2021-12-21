package com.inbobwetrust.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.inbobwetrust.service.DeliveryServiceTest;
import com.inbobwetrust.util.vo.DeliveryInstanceGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class DeliveryTest {
    Logger log = (Logger) LoggerFactory.getLogger(DeliveryServiceTest.class);

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

    @Test
    @DisplayName("[Delivery.isNew] 신규주문인지를 확인한다.")
    void isNewTest() {
        Delivery newDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);
        newDelivery.setOrderStatus(null);
        assertTrue(newDelivery.isNew());

        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.equals(OrderStatus.NEW)) {
                continue;
            }
            newDelivery.setOrderStatus(orderStatus);
            assertTrue(!newDelivery.isNew());
        }
    }

    @Test
    @DisplayName("[Delivery.isValidPickupTime] 픽업요청시간이 현재시간 이후여야한다.")
    void isValidPickupTime() {
        Delivery newDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);

        newDelivery.setPickupTime(LocalDateTime.now().minusSeconds(1));
        assertTrue(!newDelivery.isValidPickupTime());

        newDelivery.setPickupTime(LocalDateTime.now());
        assertTrue(!newDelivery.isValidPickupTime());

        newDelivery.setPickupTime(LocalDateTime.now().plusMinutes(1));
        assertTrue(newDelivery.isValidPickupTime());
    }
}
