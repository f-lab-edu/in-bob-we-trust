package com.inbobwetrust.model.entity;

import lombok.*;

import org.apache.ibatis.type.Alias;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Alias("RiderLocation")
public class RiderLocation {
    private Long riderId;
    private Double longitude;
    private Double latitude;

    @Builder
    public RiderLocation(Long riderId, Double longitude, Double latitude) {
        this.riderId = riderId;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
