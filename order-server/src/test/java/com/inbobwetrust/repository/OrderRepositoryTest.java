package com.inbobwetrust.repository;

import com.inbobwetrust.exceptions.EmptyResultSetSqlException;
import com.inbobwetrust.model.entity.Order;
import com.inbobwetrust.model.entity.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class OrderRepositoryTest {
    @Autowired OrderRepository orderRepository;

    Comparator<Order> descendingByIdComparator =
            (o1, o2) -> {
                Long idDiff = o2.getId() - o1.getId();
                if (idDiff == 0) return 0;
                if (idDiff < 0) return -1;
                if (idDiff > 0) return 1;
                throw new RuntimeException("[OrderRepositoryTest] UndefinedCompring result");
            };

    private void assertDatetimeExist(Order order) {
        assertNotNull(order.getUpdatedAt());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    @DisplayName("[OrderRepositoryTest.assertDatetimeExist]")
    private void assertDatetimeExistTest() {
        assertThrows(Exception.class, () -> assertDatetimeExist(new Order()));
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        assertDoesNotThrow(() -> assertDatetimeExist(order));
    }

    @Test
    @DisplayName("[OrderRepository.findAll] 성공 : 모든 주문내역조회 실행가능")
    void findAllTest_1() {
        assertTrue(!orderRepository.findAll().isEmpty());
        assertDoesNotThrow(() -> orderRepository.findAll());
        orderRepository.findAll().stream().forEach(this::assertDatetimeExist);
    }

    @Test
    @DisplayName("[OrderRepository.findAll] 성공 : 모든 주문내역조회 insert 후 변경내용 조회")
    void findAllTest_2() {
        int addedRows = 10;
        for (int i = 0; i < addedRows; i++) {
            List<Order> orders = orderRepository.findAll();
            assertTrue(!orders.isEmpty());
            Order lastOrder = orders.get(orders.size() - 1);
            orderRepository.save(makeNewOrderFrom(lastOrder));

            int expected = orders.size() + 1;
            int actual = orderRepository.findAll().size();
            assertEquals(expected, actual);
        }
    }

    @Test
    @DisplayName("[tbl_order] 주문테이블 AutoIncrement 테스트, 1부터 시작해야한다.")
    void autoIncrementTest() {
        List<Order> orders = orderRepository.findAll();
        Order newOrder = makeNewOrderFrom(orders.get(orders.size() - 1));
        assertTrue(!orders.isEmpty());

        int addedRows = 100;
        for (int i = 0; i < addedRows; i++) {
            orderRepository.save(newOrder);
        }
        List<Order> newOrders = orderRepository.findAll();
        int expectedNumRows = orders.size() + addedRows;
        int actualNumRows = newOrders.size();
        assertEquals(expectedNumRows, actualNumRows);
        for (int expectedId = 1; expectedId <= actualNumRows; expectedId++) {
            Long actualId = newOrders.get(expectedId - 1).getId();
            assertEquals(expectedId, actualId);
        }
    }

    private Order makeNewOrderFrom(Order lastOrder) {
        return Order.builder()
                .shopId(lastOrder.getShopId())
                .customerId(lastOrder.getCustomerId())
                .address(lastOrder.getAddress())
                .orderStatus(OrderStatus.getInitial())
                .phoneNumber(lastOrder.getPhoneNumber())
                .build();
    }

    @Test
    @DisplayName("[OrderRepository.save] 주문저장 성공")
    void saveTest_1() {
        final List<Order> savedOrders = orderRepository.findAll();
        final Order newOrder = makeNewOrderFrom(savedOrders.get(savedOrders.size() - 1));

        orderRepository.save(newOrder);
        final int expected = savedOrders.size() + 1;
        final int actual = orderRepository.findAll().size();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("[OrderRepository.save] 주문저장시 생성일, 수정일 신규값 생성")
    void saveTest_4() {
        List<Order> savedOrders = orderRepository.findAll();
        Collections.sort(savedOrders, descendingByIdComparator);
        Order newOrder = makeNewOrderFrom(savedOrders.get(0));
        String uniqueInformation = LocalDateTime.now().toString();
        newOrder.setAddress(uniqueInformation);

        assertNull(newOrder.getUpdatedAt());
        assertNull(newOrder.getCreatedAt());
        orderRepository.save(newOrder);
        Order savedOrder =
                orderRepository
                        .findByOrderId(savedOrders.get(0).getId() + 1)
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertTrue(savedOrder.getAddress().equals(uniqueInformation));
        assertNotNull(savedOrder.getCreatedAt());
        assertNotNull(savedOrder.getCreatedAt());
    }

    @Test
    @DisplayName("[OrderRepository.save] 빈값 전송")
    void saveTest_2() {
        assertThrows(Exception.class, () -> orderRepository.save(null));
        assertThrows(Exception.class, () -> orderRepository.save(new Order()));
    }

    @Test
    @DisplayName("[OrderRepository.save] customerId NOT NULL")
    void saveTest_3() {
        Order newOrder = makeNewOrderFrom(orderRepository.findAll().get(0));
        newOrder.setCustomerId(null);
        Assertions.assertThrows(Exception.class, () -> orderRepository.save(newOrder))
                .printStackTrace();
    }

    @Test
    @DisplayName("[OrderRepository.save] shopId NOT NULL")
    void saveTest_5() {
        Order newOrder = makeNewOrderFrom(orderRepository.findAll().get(0));
        newOrder.setShopId(null);
        Assertions.assertThrows(Exception.class, () -> orderRepository.save(newOrder))
                .printStackTrace();
    }

    @Test
    @DisplayName("[OrderRepository.update]")
    void updateTest_1() {
        List<Order> orders = orderRepository.findAll();
        Collections.sort(orders, descendingByIdComparator);
        Order lastOrder = orders.get(0);

        lastOrder.setAddress(lastOrder.getAddress().concat("-updated"));
        lastOrder.setCustomerId(lastOrder.getCustomerId() + 1);
        lastOrder.setOrderStatus(lastOrder.getOrderStatus().returnSomethingElse());
        lastOrder.setPhoneNumber(lastOrder.getPhoneNumber().concat("-updated"));
        int affectedRows = orderRepository.update(lastOrder);
        assertEquals(1, affectedRows);

        Order updatedOrder =
                orderRepository
                        .findByOrderId(lastOrder.getId())
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertTrue(updatedOrder.getAddress().equals(lastOrder.getAddress()));
    }

    @Test
    @DisplayName("[OrderRepository.update] createdAt은 insert시 업데이트")
    void updateTest_2() {
        List<Order> orders = orderRepository.findAll();
        Collections.sort(orders, descendingByIdComparator);
        Order lastOrder = orders.get(0);

        lastOrder.setOrderStatus(lastOrder.getOrderStatus().returnSomethingElse());
        int affectedRows = orderRepository.update(lastOrder);
        assertEquals(1, affectedRows);

        Order updatedOrder =
                orderRepository
                        .findByOrderId(lastOrder.getId())
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertNotEquals(lastOrder.getUpdatedAt(), updatedOrder.getUpdatedAt());
        assertEquals(lastOrder.getId(), updatedOrder.getId());
        assertEquals(lastOrder.getAddress(), updatedOrder.getAddress());
        assertEquals(lastOrder.getCustomerId(), updatedOrder.getCustomerId());
        assertEquals(lastOrder.getPhoneNumber(), updatedOrder.getPhoneNumber());
        assertEquals(lastOrder.getCreatedAt(), updatedOrder.getCreatedAt());
    }

    @Test
    @DisplayName("[OrderRepository.update] updatedAt은 실제값 변경 발생시 변경")
    void updateTest_3() {
        List<Order> orders = orderRepository.findAll();
        Collections.sort(orders, descendingByIdComparator);
        Order lastOrder = orders.get(0);
        lastOrder.setOrderStatus(lastOrder.getOrderStatus().returnSomethingElse());

        int affectedRows = orderRepository.update(lastOrder);

        assertEquals(1, affectedRows);
        Order updatedOrder =
                orderRepository
                        .findByOrderId(lastOrder.getId())
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertNotEquals(lastOrder.getUpdatedAt(), updatedOrder.getUpdatedAt());
    }

    @Test
    @DisplayName("[OrderRepository.update] updatedAt은 실제값 변경 없을시 변경 안됨")
    void updateTest_4() {
        List<Order> orders = orderRepository.findAll();
        Collections.sort(orders, descendingByIdComparator);
        Order lastOrder = orders.get(0);

        int affectedRows = orderRepository.update(lastOrder);

        assertEquals(1, affectedRows);
        Order updatedOrder =
                orderRepository
                        .findByOrderId(lastOrder.getId())
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertEquals(lastOrder.getCreatedAt(), updatedOrder.getCreatedAt());
    }

    @Test
    @DisplayName("[OrderRepository.findByOrderId] 주문 조회 성공")
    void findByOrderIdTest() {
        final List<Order> savedOrders = orderRepository.findAll();
        final int expectedSize = savedOrders.size() + 1;
        final Order lastOrder = savedOrders.get(savedOrders.size() - 1);
        final Order newOrder = makeNewOrderFrom(lastOrder);
        newOrder.setOrderStatus(newOrder.getOrderStatus().returnSomethingElse());

        assertDoesNotThrow(() -> orderRepository.save(newOrder));

        final int actualSize = orderRepository.findAll().size();
        assertEquals(expectedSize, actualSize);
        Order order =
                orderRepository
                        .findByOrderId(lastOrder.getId() + 1)
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertDatetimeExist(order);
    }

    @Test
    @DisplayName("[OrderRepository.findByOrderId] 미존재 주문 조회")
    void findByOrderIdTest2() {
        List<Order> savedOrders = orderRepository.findAll();

        Collections.sort(savedOrders, descendingByIdComparator);

        Order orderWithLargestId = savedOrders.get(0);
        Optional<Order> noResult = orderRepository.findByOrderId(orderWithLargestId.getId() + 1);
        assertThrows(
                EmptyResultSetSqlException.class,
                () -> noResult.orElseThrow(EmptyResultSetSqlException::new));
    }
}
