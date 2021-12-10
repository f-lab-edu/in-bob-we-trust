package com.inbobwetrust.model.vo;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Delivery {
    private String shopIp;
    private String orderId;

    public Delivery() {}

    public Delivery(String shopIp, String orderId) {
        this.shopIp = shopIp;
        this.orderId = orderId;
    }
}
