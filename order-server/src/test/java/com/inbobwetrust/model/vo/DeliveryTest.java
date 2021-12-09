package com.inbobwetrust.model.vo;

import com.inbobwetrust.util.vo.DeliveryInstanceGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryTest {

    @Test
    @DisplayName("Delivery VO의 equals 테스트")
    void equals_test() {
        LocalDateTime now = LocalDateTime.now();
        Delivery delivery1 = makeDelivery(now);
        Delivery delivery2 = makeDelivery(now);

        assertEquals(delivery1, delivery2);
    }

    private Delivery makeDelivery(LocalDateTime now) {
        return Delivery.builder()
                .orderId("order-1")
                .riderId("rider-1")
                .deliveryAgentId("agent-1")
                .wantedPickupTime(now.plusMinutes(30))
                .estimatedDeliveryFinishTime(now.plusMinutes(60))
                .build();
    }

}
