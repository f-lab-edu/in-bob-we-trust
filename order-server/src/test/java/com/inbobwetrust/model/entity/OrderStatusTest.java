package com.inbobwetrust.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderStatusTest {

    @Test
    @DisplayName("[OrderStatus.equals] 주문상태 동일성 체크")
    void equalsTest() {
        assertTrue(OrderStatus.NEW.equals(OrderStatus.NEW));
        assertTrue(OrderStatus.ACCEPTED.equals(OrderStatus.ACCEPTED));
        assertTrue(OrderStatus.DECLINED.equals(OrderStatus.DECLINED));
        assertTrue(OrderStatus.PICKED_UP.equals(OrderStatus.PICKED_UP));
        assertTrue(OrderStatus.COMPLETE.equals(OrderStatus.COMPLETE));
    }

    @Test
    @DisplayName("[OrderStatus.getNext] 다음 상태를 리턴, 비즈니스로직 명시적")
    void getNextTest() {
        OrderStatus.NEW.getNext().equals(OrderStatus.ACCEPTED);
        OrderStatus.ACCEPTED.getNext().equals(OrderStatus.DECLINED);
        OrderStatus.DECLINED.getNext().equals(OrderStatus.PICKED_UP);
        OrderStatus.PICKED_UP.getNext().equals(OrderStatus.COMPLETE);
        assertThrows(IllegalStateException.class, () -> OrderStatus.COMPLETE.getNext());
    }

    @Test
    @DisplayName("[OrderStatus.returnSomethingElse] 현재상태와 다른 임의의 상태 리턴")
    void returnSomethingElseTest() {
        int resonableNumberOfAttempts = 100;
        for (int i = 0; i < resonableNumberOfAttempts; i++) {
            for (OrderStatus progress : OrderStatus.values()) {
                assertDoesNotThrow(() -> assertNotEquals(progress, progress.returnSomethingElse()));
            }
        }
    }

    @Test
    @DisplayName("[OrderStatus.getInitial] 신규주문접수에 대한 초기상태")
    void getInitialTest() {
        OrderStatus.getInitial().equals(OrderStatus.NEW);
    }
}
