package com.inbobwetrust.model.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class RiderLocationDto {

    @NotNull(message = "주문번호는 필수값입니다.")
    @Min(value = 1, message = "라이더번호는 최소 1 이상이어야 합니다.")
    private Long riderId;

    @NotNull(message = "Longitude 는 필수값입니다.")
    @DecimalMin(value = "-180.0", message = "Longitude 는 최소 -180 입니다.")
    @DecimalMax(value = "180.0", message = "Longitude 는 최대 180 입니다.")
    private Double longitude;

    @NotNull(message = "Latitude 는 필수값입니다.")
    @DecimalMin(value = "-90.0", message = "Latitude 는 최소 -90 입니다.")
    @DecimalMax(value = "90.0", message = "Latitude 는 최대 90 입니다.")
    private Double latitude;

    @Builder
    public RiderLocationDto(Long riderId, Double longitude, Double latitude) {
        this.riderId = riderId;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
