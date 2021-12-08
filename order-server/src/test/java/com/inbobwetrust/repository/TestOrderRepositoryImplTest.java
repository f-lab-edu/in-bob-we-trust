package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class TestOrderRepositoryImplTest {

    private TestOrderRepositoryImpl orderRepository = new TestOrderRepositoryImpl();
    Order orderToSave = Order.builder().id("order-1").shopId("shop-1").build();
    Order notExistOrder = Order.builder().id("order-notexist").shopId("shop-notexist").build();

    @BeforeEach
    public void setUp() {
        orderRepository.clear();
    }

    @Test
    @DisplayName("신규주문 저장")
    void save_successTest() {
        Assertions.assertTrue(0 == orderRepository.findAll().size());

        orderRepository.save(orderToSave);

        Assertions.assertTrue(1 == orderRepository.findAll().size());
    }

    @Test
    @DisplayName("같은 주문 ID로 저장")
    void save_failTest() {
        Assertions.assertTrue(orderRepository.save(orderToSave));
        Assertions.assertFalse(orderRepository.save(orderToSave));
    }

    @Test
    @DisplayName("저장한 주문 아이디로 검색 성공")
    void findByOrderId_successTest() {
        Assertions.assertTrue(0 == orderRepository.findAll().size());
        orderRepository.save(orderToSave);
        Assertions.assertTrue(1 == orderRepository.findAll().size());

        Optional<Order> savedOrder = orderRepository.findByOrderId(orderToSave.getId());

        Assertions.assertTrue(savedOrder.isPresent());
        Assertions.assertTrue(savedOrder.get().getId().equals(orderToSave.getId()));
    }

    @Test
    @DisplayName("저장하지 않은 주문 아이디로 검색 실패")
    void findByOrderId_failTest() {
        Assertions.assertTrue(0 == orderRepository.findAll().size());

        orderRepository.save(orderToSave);

        Optional<Order> emptyOrderResult = orderRepository.findByOrderId(notExistOrder.getId());

        Assertions.assertTrue(emptyOrderResult.isEmpty());
    }

    @Test
    @DisplayName("전체 주문데이터 조회")
    void findAll_successTest() {
        Assertions.assertTrue(orderRepository.findAll().size() == 0);
        for (int i = 0; i < 10; i++) {
            orderRepository.save(Order.builder().id("order-" + i).shopId("shop-" + i).build());
        }

        Assertions.assertEquals(10, orderRepository.findAll().size());
    }
}
