package com.inbobwetrust.util.vo;

import com.inbobwetrust.model.entity.Delivery;

import java.time.LocalDateTime;
import java.util.List;

public class DeliveryInstanceGenerator {
    public static List<Delivery> makeDeliveryForRequestAndResponse() {
        LocalDateTime wantedPickupTime = LocalDateTime.now().plusMinutes(30);
        LocalDateTime estimatedDeliveryFinishTime = wantedPickupTime.plusMinutes(30);
        Delivery deliveryRequest =
                Delivery.builder()
                        .orderId(1L)
                        .riderId(1L)
                        .agencyId(1L)
                        .pickupTime(wantedPickupTime)
                        .build();
        Delivery expectedDeliveryResponse =
                Delivery.builder()
                        .orderId(deliveryRequest.getOrderId())
                        .riderId(deliveryRequest.getRiderId())
                        .agencyId(deliveryRequest.getAgencyId())
                        .pickupTime(deliveryRequest.getPickupTime())
                        .finishTime(estimatedDeliveryFinishTime)
                        .build();
        return List.of(deliveryRequest, expectedDeliveryResponse);
    }

    public static Delivery makeSimpleNumberedDelivery(long num) {
        LocalDateTime now = LocalDateTime.now();
        Delivery delivery =
                Delivery.builder()
                        .orderId(num)
                        .riderId(num)
                        .agencyId(num)
                        .pickupTime(now.plusMinutes(30))
                        .finishTime(now.plusMinutes(60))
                        .build();
        return delivery;
    }
}
