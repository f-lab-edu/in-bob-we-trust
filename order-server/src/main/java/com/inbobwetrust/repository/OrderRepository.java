package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    boolean save(Order order);

    Optional<Order> findByOrderId(String id);

    List<Order> findAll();
}
