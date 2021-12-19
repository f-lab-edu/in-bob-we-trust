package com.inbobwetrust.model.entity;

import lombok.*;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Alias("Order")
public class Order {
    private Long id;

    private Long customerId;

    private Long shopId;

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
            OrderStatus orderStatus,
            String address,
            String phoneNumber
            ) {
        this.id = id;
        this.customerId = customerId;
        this.shopId = shopId;
        this.orderStatus = orderStatus;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
