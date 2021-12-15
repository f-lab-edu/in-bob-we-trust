package com.inbobwetrust.model.entitiy;

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

    @Builder
    public Rider(Long id, Long agencyId) {
        this.id = id;
        this.agencyId = agencyId;
    }
}
