package com.inbobwetrust.model.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class Delivery {
    private Long orderId;

    private Long riderId;

    private LocalDateTime wantedPickupTime;

    private LocalDateTime estimatedCookingTime;

    private LocalDateTime estimatedDeliveryFinishTime;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    @Builder
    public Delivery(
            Long orderId,
            Long riderId,
            LocalDateTime wantedPickupTime,
            LocalDateTime estimatedCookingTime,
            LocalDateTime estimatedDeliveryFinishTime,
            LocalDateTime regDate,
            LocalDateTime modDate) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.wantedPickupTime = wantedPickupTime;
        this.estimatedCookingTime = estimatedCookingTime;
        this.estimatedDeliveryFinishTime = estimatedDeliveryFinishTime;
        this.regDate = regDate;
        this.modDate = modDate;
    }
}
