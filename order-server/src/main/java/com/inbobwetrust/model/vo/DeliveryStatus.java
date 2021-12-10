package com.inbobwetrust.model.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryStatus {
    private String orderId;
    private String status;

    protected DeliveryStatus() {}

    public DeliveryStatus(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }
}
