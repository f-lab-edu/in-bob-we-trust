package com.inbobwetrust.model.vo;

import com.inbobwetrust.model.entity.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    @DisplayName("주문 VO의 equals 오버라이드")
    void equals_test() {
        Order order1 = Order.builder().id(1L).shopId(1L).build();
        Order order2 = Order.builder().id(2L).shopId(2L).build();
        Order order3 = Order.builder().id(1L).shopId(1L).build();

        Assertions.assertFalse(order1.equals(order2));
        Assertions.assertTrue(order1.equals(order3));
    }
}
