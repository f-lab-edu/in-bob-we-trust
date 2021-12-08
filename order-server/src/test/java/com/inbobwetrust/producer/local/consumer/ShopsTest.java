package com.inbobwetrust.producer.local.consumer;

import com.inbobwetrust.model.vo.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;

import static org.junit.jupiter.api.Assertions.*;

class ShopsTest {

    Shops shops = new Shops();

    @Test
    @DisplayName("사장님에게 외부 요청 전달성공")
    void send_Test() {
        int totalOrders = 3;
        int totalShops = 2;
        for (int i = 0; i < totalOrders; i++) {
            shops.send(Order.builder().id("order-" + i).shopId("shop-1").build());
            shops.send(Order.builder().id("order-" + i).shopId("shop-2").build());
        }

        Assertions.assertTrue(totalOrders * totalShops == shops.getHistory().size());
        Assertions.assertTrue(totalShops == shops.shopsCount());
    }
}
