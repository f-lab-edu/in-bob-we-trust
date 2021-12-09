package com.inbobwetrust.util.vo;

import com.inbobwetrust.model.vo.Delivery;

import java.time.LocalDateTime;
import java.util.List;

public class DeliveryInstanceGenerator {
    public static List<Delivery> makeDeliveryForRequestAndResponse() {
        LocalDateTime wantedPickupTime = LocalDateTime.now().plusMinutes(30);
        LocalDateTime estimatedDeliveryFinishTime = wantedPickupTime.plusMinutes(30);
        Delivery deliveryRequest =
                Delivery.builder()
                        .orderId("order-1")
                        .riderId("rider-1")
                        .wantedPickupTime(wantedPickupTime)
                        .build();
        Delivery expectedDeliveryResponse =
                Delivery.builder()
                        .orderId(deliveryRequest.getOrderId())
                        .riderId(deliveryRequest.getRiderId())
                        .wantedPickupTime(deliveryRequest.getWantedPickupTime())
                        .estimatedDeliveryFinishTime(estimatedDeliveryFinishTime)
                        .build();
        return List.of(deliveryRequest, expectedDeliveryResponse);
    }
}
