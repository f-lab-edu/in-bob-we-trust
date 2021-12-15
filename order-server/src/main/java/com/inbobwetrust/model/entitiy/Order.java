package com.inbobwetrust.model.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class Order {
    private Long id;

    private String customerId; // 고객아이디

    private Long shopId; // 가게아이디

    private OrderStatus orderStatus; // 주문상태

    private String address; // 배달주소

    private String phoneNumber; // 안심번호

    private Payment payment;

    private LocalDateTime createdAt; // 생성일

    private LocalDateTime updatedAt; // 수정일
}
