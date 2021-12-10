package com.inbobwetrust.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Agency {
    private Long id;

    private String name;

    private AgencyStatus status;

    @Builder
    public Agency(Long id, String name, AgencyStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}
