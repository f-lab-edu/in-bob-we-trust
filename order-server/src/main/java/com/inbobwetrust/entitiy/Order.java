package com.inbobwetrust.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class Order {
    private Long id;

    private String customerId;

    private Long shopId;

    private String riderId;

    private OrderStatus status;

    private PaymentType paymentType;

    private Long paymentPrice;

    private String address;

    private String customerRequest;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    private LocalDateTime orderStatusLastUpdated;

    @Builder
    public Order(Long id, String customerId, Long shopId, String riderId, OrderStatus status, PaymentType paymentType,
                 Long paymentPrice, String address, String customerRequest, LocalDateTime regDate, LocalDateTime modDate,
                 LocalDateTime orderStatusLastUpdated) {
        this.id = id;
        this.customerId = customerId;
        this.shopId = shopId;
        this.riderId = riderId;
        this.status = status;
        this.paymentType = paymentType;
        this.paymentPrice = paymentPrice;
        this.address = address;
        this.customerRequest = customerRequest;
        this.regDate = regDate;
        this.modDate = modDate;
        this.orderStatusLastUpdated = orderStatusLastUpdated;
    }
}
