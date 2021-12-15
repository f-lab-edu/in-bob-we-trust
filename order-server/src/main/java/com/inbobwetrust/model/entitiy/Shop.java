package com.inbobwetrust.model.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Shop {
    private String id;

    private String endpoint;

    @Builder
    public Shop(String id, String endpoint) {
        this.id = id;
        this.endpoint = endpoint;
    }
}
