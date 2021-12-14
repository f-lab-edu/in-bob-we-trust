package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.producer.OrderProducer;
import com.inbobwetrust.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public Order receiveNewOrder(Order order) {
        if (!orderRepository.save(order)) {
            throw new RuntimeException("Save Operation Failed : order with such ID exists");
        }
        Order savedOrder =
                orderRepository
                        .findByOrderId(order.getId())
                        .orElseThrow(() -> new RuntimeException("Cannot find saved order"));

        orderProducer.sendNewOrderMessage(savedOrder);
        return savedOrder;
    }
}
