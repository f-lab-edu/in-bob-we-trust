package com.inbobwetrust.model.vo;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class Order {
    private String id;
    private String shopId;

    public Order(String id, String shopId) {
        this.id = id;
        this.shopId = shopId;
    }
}
