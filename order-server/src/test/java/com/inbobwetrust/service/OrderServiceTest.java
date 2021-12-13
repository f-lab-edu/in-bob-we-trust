package com.inbobwetrust.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.producer.OrderProducer;
import com.inbobwetrust.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
class OrderServiceTest {

    @InjectMocks OrderService orderService;

    @Mock OrderRepository orderRepository;

    @Mock OrderProducer orderProducer;

    @Test
    @DisplayName("신규주문수신 : 성공적으로 저장하는 케이스")
    void receiveNewOrder_successTest() {
        Order orderReceived = Order.builder().id("order-1").shopId("shop-1").build();
        when(orderRepository.save(orderReceived)).thenReturn(true);
        when(orderRepository.findByOrderId(orderReceived.getId()))
                .thenReturn(Optional.ofNullable(orderReceived));

        Order orderSaved = orderService.receiveNewOrder(orderReceived);

        verify(orderRepository, times(1)).save(orderReceived);
        verify(orderProducer, times(1)).sendNewOrderMessage(orderSaved);
    }

    @Test
    @DisplayName("신규주문수신 : 주문저장 실패 시 exception 발생")
    void receiveNewOrder_fail_repository_saveTest() {
        Order orderReceived = Order.builder().id("order-1").shopId("shop-1").build();
        when(orderRepository.save(orderReceived)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> orderService.receiveNewOrder(orderReceived))
                .printStackTrace();

        verify(orderRepository, times(1)).save(orderReceived);
        verify(orderProducer, times(0)).sendNewOrderMessage(orderReceived);
    }
}
