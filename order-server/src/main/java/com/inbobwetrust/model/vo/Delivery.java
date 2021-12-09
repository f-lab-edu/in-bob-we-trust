package com.inbobwetrust.model.vo;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Delivery {
    private String orderId;
    private String riderId;
    private String deliveryAgentId;
    private LocalDateTime wantedPickupTime;
    private LocalDateTime estimatedDeliveryFinishTime;

    protected Delivery() {}

    public Delivery(String orderId, String riderId, LocalDateTime wantedPickupTime) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.wantedPickupTime = wantedPickupTime;
    }

    public Delivery(
            String orderId,
            String riderId,
            String deliveryAgentId,
            LocalDateTime wantedPickupTime,
            LocalDateTime estimatedDeliveryFinishTime) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.deliveryAgentId = deliveryAgentId;
        this.wantedPickupTime = wantedPickupTime;
        this.estimatedDeliveryFinishTime = estimatedDeliveryFinishTime;
    }
}
