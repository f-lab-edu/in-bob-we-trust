package com.inbobwetrust.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class DeliverySetRiderDto {
    @NotNull(message = "주문번호는 필수값입니다.")
    @Min(value = 1, message = "주문번호는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long orderId;

    @NotNull(message = "rider_id는 필수값입니다.")
    @Min(value = 1, message = "rider_id는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long riderId;

    @NotNull(message = "agency_id는 필수값입니다.")
    @Min(value = 1, message = "agency_id는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long agencyId;

    @Builder
    public DeliverySetRiderDto(Long orderId, Long riderId, Long agencyId) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.agencyId = agencyId;
    }
}
