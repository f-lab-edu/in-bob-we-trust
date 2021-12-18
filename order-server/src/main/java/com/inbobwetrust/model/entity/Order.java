package com.inbobwetrust.model.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Order {
    private Long id;

    private Long customerId;

    private Long shopId;

    private Long deliveryId;

    private OrderStatus orderStatus;

    private String address;

    private String phoneNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Order(
            Long id,
            Long customerId,
            Long shopId,
            Long deliveryId,
            OrderStatus orderStatus,
            String address,
            String phoneNumber,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.shopId = shopId;
        this.deliveryId = deliveryId;
        this.orderStatus = orderStatus;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
