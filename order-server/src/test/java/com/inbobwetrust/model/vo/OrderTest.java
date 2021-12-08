package com.inbobwetrust.model.vo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;

class OrderTest {

    @Test
    @DisplayName("주문 VO의 equals 오버라이드")
    void equals_test() {
        Order order1 = new Order("order1", "shop1");
        Order order2 = new Order("order2", "shop2");
        Order order3 = new Order("order1", "shop1");

        Assertions.assertFalse(order1.equals(order2));
        Assertions.assertTrue(order1.equals(order3));
    }
}
