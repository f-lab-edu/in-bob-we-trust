package com.inbobwetrust.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Alias("DeliveryStatusDto")
public class DeliveryGetStatusDto {
    @NotNull(message = "주문번호는 필수값입니다.")
    @Min(value = 1, message = "주문번호는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long orderId;

    @Builder
    public DeliveryGetStatusDto(Long orderId) {
        this.orderId = orderId;
    }
}
