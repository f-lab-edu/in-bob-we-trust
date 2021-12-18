package com.inbobwetrust.model.entity;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Rider {
    private Long id;
    private Long agencyId;

    @Builder
    public Rider(Long id, Long agencyId) {
        this.id = id;
        this.agencyId = agencyId;
    }

    public boolean hasSameID(Rider rider) {
        return id.longValue() == rider.id.longValue();
    }
}
