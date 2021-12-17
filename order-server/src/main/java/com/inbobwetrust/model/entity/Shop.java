package com.inbobwetrust.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Shop {
    private Long id;

    private String endpoint;

    @Builder
    public Shop(Long id, String endpoint) {
        this.id = id;
        this.endpoint = endpoint;
    }
}
