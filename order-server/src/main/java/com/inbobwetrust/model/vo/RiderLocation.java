package com.inbobwetrust.model.vo;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class RiderLocation {
    private String riderId;
    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime lastUpdated;

    protected RiderLocation() {}

    public RiderLocation(String riderId, String location, LocalDateTime lastUpdated) {
        this.riderId = riderId;
        this.location = location;
        this.lastUpdated = lastUpdated;
    }
}
