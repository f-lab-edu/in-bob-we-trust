package com.inbobwetrust.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class OrderMenu {
    private Long menuId;

    private Long orderId;

    private int quantity;

    @Builder
    public OrderMenu(Long menuId, Long orderId, int quantity) {
        this.menuId = menuId;
        this.orderId = orderId;
        this.quantity = quantity;
    }
}
