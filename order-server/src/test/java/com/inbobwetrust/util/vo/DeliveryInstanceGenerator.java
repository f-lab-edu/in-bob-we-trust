package com.inbobwetrust.util.vo;

import com.inbobwetrust.model.vo.Delivery;

import java.time.LocalDateTime;

public class DeliveryInstanceGenerator {

    public static Delivery makeSimpleNumberedDelivery(int num) {
        LocalDateTime now = LocalDateTime.now();
        Delivery delivery =
            Delivery.builder()
                .orderId("order-" + num)
                .riderId("rider-" + num)
                .deliveryAgentId("agent-" + num)
                .wantedPickupTime(now.plusMinutes(30))
                .estimatedDeliveryFinishTime(now.plusMinutes(60))
                .build();
        return delivery;
    }
}
