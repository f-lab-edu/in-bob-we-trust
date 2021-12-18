package com.inbobwetrust.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Rider {
    private Long id;
    private Long agencyId;
    private Location location;

    @Builder
    public Rider(Long id, Long agencyId, Location location) {
        this.id = id;
        this.agencyId = agencyId;
        this.location = location;
    }

    public boolean hasSameID(Rider rider) {
        return id.longValue() == rider.id.longValue();
    }
}
