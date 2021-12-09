package com.inbobwetrust.model.vo;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Rider {
    private String orderId;
    private String riderId;
    private String location;
    @Nullable
    private LocalDateTime lastUpdated;

    protected Rider() {}

    public Rider(String orderId, String riderId, String location) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.location = location;
    }

    public Rider(String orderId, String riderId, String location, @Nullable LocalDateTime lastUpdated) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.location = location;
        this.lastUpdated = lastUpdated;
    }
}
