package com.inbobwetrust.model.vo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;

class OrderTest {

    @Test
    @DisplayName("주문 VO의 equals 오버라이드")
    void equals_test() {
        Order order1 = Order.builder().id("order1").shopId("shop1").build();
        Order order2 = Order.builder().id("order2").shopId("shop2").build();
        Order order3 = Order.builder().id("order1").shopId("shop1").build();

        Assertions.assertFalse(order1.equals(order2));
        Assertions.assertTrue(order1.equals(order3));
    }
}
