package com.inbobwetrust.producer.local.consumer;

import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.producer.local.consumer.neworder.Shops;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShopsTest {

    Shops shops = new Shops();

    @Test
    @DisplayName("사장님에게 외부 요청 전달성공")
    void receive_Test() {
        int totalOrders = 3;
        int totalShops = 2;
        for (int i = 0; i < totalOrders; i++) {
            shops.receive(Order.builder().id("order-" + i).shopId("shop-1").build());
            shops.receive(Order.builder().id("order-" + i).shopId("shop-2").build());
        }

        Assertions.assertTrue(totalOrders * totalShops == shops.getHistory().size());
        Assertions.assertTrue(totalShops == shops.shopsCount());
    }
}
