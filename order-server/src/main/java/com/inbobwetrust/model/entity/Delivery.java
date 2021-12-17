package com.inbobwetrust.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class Delivery {
    private Long id;

    private Long orderId;

    private Long riderId;

    private Long agencyId;

    private LocalDateTime pickupTime;

    private LocalDateTime finishTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Delivery(
            Long id,
            Long orderId,
            Long riderId,
            Long agencyId,
            LocalDateTime pickupTime,
            LocalDateTime finishTime,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.riderId = riderId;
        this.agencyId = agencyId;
        this.pickupTime = pickupTime;
        this.finishTime = finishTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
