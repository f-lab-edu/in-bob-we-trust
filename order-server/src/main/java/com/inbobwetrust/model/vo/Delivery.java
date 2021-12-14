package com.inbobwetrust.model.vo;

import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class Delivery {

    private String orderId;
    private String riderId;
    private String shopEndpoint;
    private String deliveryAgentId;
    private String deliveryAgentEndpoint;
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime wantedPickupTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime estimatedDeliveryFinishTime;

    public Delivery(String orderId, String riderId, LocalDateTime wantedPickupTime) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.wantedPickupTime = wantedPickupTime;
    }

    public Delivery(
            String orderId,
            String riderId,
            String shopEndpoint,
            String deliveryAgentId,
            String deliveryAgentEndpoint,
            String status,
            LocalDateTime wantedPickupTime,
            LocalDateTime estimatedDeliveryFinishTime) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.shopEndpoint = shopEndpoint;
        this.deliveryAgentId = deliveryAgentId;
        this.deliveryAgentEndpoint = deliveryAgentEndpoint;
        this.status = status;
        this.wantedPickupTime = wantedPickupTime;
        this.estimatedDeliveryFinishTime = estimatedDeliveryFinishTime;
    }
}
