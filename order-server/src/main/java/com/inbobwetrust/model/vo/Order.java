package com.inbobwetrust.model.vo;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Order {
    private String id;
    private String shopId;

    public Order(String id, String shopId) {
        this.id = id;
        this.shopId = shopId;
    }
}
