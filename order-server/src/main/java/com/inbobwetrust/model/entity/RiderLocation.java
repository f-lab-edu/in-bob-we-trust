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
    private double longitude;
    private double latitude;

    @Builder
    public RiderLocation(Long riderId, double longitude, double latitude) {
        this.riderId = riderId;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
