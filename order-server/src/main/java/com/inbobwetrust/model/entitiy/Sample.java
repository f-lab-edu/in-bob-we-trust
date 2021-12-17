package com.inbobwetrust.model.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Sample {
    private Long id;

    @Builder
    public Sample(Long id) {
        this.id = id;
    }
}
