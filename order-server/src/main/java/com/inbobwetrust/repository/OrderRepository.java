package com.inbobwetrust.repository;

import com.inbobwetrust.model.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    boolean save(Order order);

    Optional<Order> findByOrderId(Long id);

    List<Order> findAll();

    int update(Order lastOrder);
}
