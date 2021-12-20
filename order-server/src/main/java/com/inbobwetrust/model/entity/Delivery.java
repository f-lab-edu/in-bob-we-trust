package com.inbobwetrust.model.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class Delivery {
    private Long id;

    private Long orderId;

    private Long riderId;

    private Long agencyId;

    private OrderStatus orderStatus;

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
            OrderStatus orderStatus,
            LocalDateTime pickupTime,
            LocalDateTime finishTime) {
        this.id = id;
        this.orderId = orderId;
        this.riderId = riderId;
        this.agencyId = agencyId;
        this.orderStatus = orderStatus;
        this.pickupTime = pickupTime;
        this.finishTime = finishTime;
    }

    public Delivery deepCopy() {
        return new Delivery(
                this.id,
                this.orderId,
                this.riderId,
                this.agencyId,
                this.orderStatus,
                this.pickupTime,
                this.finishTime,
                this.createdAt,
                this.updatedAt);
    }

    public DeliveryStatus toDeliveryStatus() {
        return DeliveryStatus.builder().orderId(this.orderId).orderStatus(this.orderStatus).build();
    }
}
