package com.inbobwetrust.model.entity;

import lombok.*;

import org.apache.ibatis.type.Alias;

@Getter
@Setter
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
