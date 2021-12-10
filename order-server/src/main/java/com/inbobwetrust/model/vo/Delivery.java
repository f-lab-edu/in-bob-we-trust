package com.inbobwetrust.model.vo;

import lombok.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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
    private String shopIp;
    private String deliveryAgentId;
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

    public Delivery(String orderId, String riderId, String shopIp, String deliveryAgentId, String status, LocalDateTime wantedPickupTime, LocalDateTime estimatedDeliveryFinishTime) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.shopIp = shopIp;
        this.deliveryAgentId = deliveryAgentId;
        this.status = status;
        this.wantedPickupTime = wantedPickupTime;
        this.estimatedDeliveryFinishTime = estimatedDeliveryFinishTime;
    }
}
