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

    private Long orderId; // 주문번호

    private String riderId; // 라이더번호 : 배차완료시 지정

    private Long agencyId; // 배달대행사번호 : 주문접수시 지정

    private LocalDateTime pickupTime; // PickupTime

    private LocalDateTime finishTime; // 배달완료시간

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
