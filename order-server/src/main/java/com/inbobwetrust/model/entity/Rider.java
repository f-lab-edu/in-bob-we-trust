package com.inbobwetrust.model.entity;

import lombok.*;

import org.apache.ibatis.type.Alias;

@Data
@Alias("Rider")
public class Rider {
    private Long id;
    private Long agencyId;

    @Builder
    public Rider(Long id, Long agencyId) {
        this.id = id;
        this.agencyId = agencyId;
    }

    public boolean hasAllData() {
        return id != null && agencyId != null;
    }
}
