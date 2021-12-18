package com.inbobwetrust.model.entity;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class DeliveryStatus {
    private Long orderId;
    private OrderStatus orderStatus;

    @Builder
    public DeliveryStatus(Long orderId, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }
}
