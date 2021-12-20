package com.inbobwetrust.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inbobwetrust.model.entity.OrderStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class DeliveryCreateDto {
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @NotNull(message = "(조리완료시간==픽업요청시간)는 필수값입니다.")
    private LocalDateTime pickupTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @NotNull(message = "배달건 생성일시는 필수값입니다.")
    private LocalDateTime createdAt;

    @Builder
    public DeliveryCreateDto(
            Long orderId,
            Long riderId,
            Long agencyId,
            LocalDateTime pickupTime,
            LocalDateTime createdAt) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.agencyId = agencyId;
        this.pickupTime = pickupTime;
        this.createdAt = createdAt;
    }
}
