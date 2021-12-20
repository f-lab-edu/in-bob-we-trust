package com.inbobwetrust.model.dto;

import com.inbobwetrust.model.entity.OrderStatus;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class DeliveryStatusDto {
    private Long orderId;
    private OrderStatus orderStatus;

    @Builder
    public DeliveryStatusDto(Long orderId, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }
}
