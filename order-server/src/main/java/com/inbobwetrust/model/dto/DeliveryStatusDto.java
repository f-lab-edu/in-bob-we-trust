package com.inbobwetrust.model.dto;

import com.inbobwetrust.model.entity.OrderStatus;

import lombok.*;

import org.apache.ibatis.type.Alias;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Alias("DeliveryStatusDto")
public class DeliveryStatusDto {
    @NotNull(message = "주문번호는 필수값입니다.")
    @Min(value = 1, message = "주문번호는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long orderId;

    @NotNull private OrderStatus orderStatus;

    @Builder
    public DeliveryStatusDto(Long orderId, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }
}
