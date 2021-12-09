package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.producer.OrderProducer;
import com.inbobwetrust.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public Order receiveNewOrder(Order order) {
        saveOrThrow(order, "Save Operation Failed : order with such ID exists");
        Order savedOrder = findByOrderId(order.getId());
        orderProducer.sendNewOrderMessage(savedOrder);
        return savedOrder;
    }

    private void saveOrThrow(Order order, String msg) {
        if (!orderRepository.save(order)) {
            throw new RuntimeException(msg);
        }
    }

    private Order findByOrderId(String orderId) {
        Optional<Order> order = orderRepository.findByOrderId(orderId);
        if (order.isEmpty()) {
            throw new RuntimeException("Cannot find Order");
        }
        return order.get();
    }
}
