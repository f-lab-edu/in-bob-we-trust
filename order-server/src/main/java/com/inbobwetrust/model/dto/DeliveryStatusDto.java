package com.inbobwetrust.model.dto;

import com.inbobwetrust.model.entity.OrderStatus;

import lombok.*;

import org.apache.ibatis.type.Alias;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Alias("DeliveryStatusDto")
public class DeliveryStatusDto {
    @NotNull(message = "주문번호는 필수값입니다.")
    @Min(value = 1, message = "주문번호는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long orderId;

    @NotNull(message = "라이더ID는 필수값입니다.")
    @Min(value = 1, message = "라이더ID는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long riderId;

    @NotNull(message = "배달ID 필수값입니다.")
    @Min(value = 1, message = "배달ID 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long deliveryId;

    @NotNull private OrderStatus orderStatus;

    @Builder
    public DeliveryStatusDto(Long orderId, Long riderId, Long deliveryId, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.deliveryId = deliveryId;
        this.orderStatus = orderStatus;
    }
}
