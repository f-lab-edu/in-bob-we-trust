package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Order;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TestOrderRepositoryImpl implements OrderRepository {
    private static final List<Order> orders = new ArrayList<>();

    @Override
    public boolean save(Order order) {
        if (contains(order)) return false;
        return orders.add(order);
    }

    @Override
    public Optional<Order> findByOrderId(String id) {
        return orders.stream().filter(existing -> existing.getId().equals(id)).findFirst();
    }

    @Override
    public List<Order> findAll() {
        return Collections.unmodifiableList(orders);
    }

    public void clear() {
        while (!orders.isEmpty()) {
            orders.remove(0);
        }
    }

    private boolean contains(Order order) {
        return orders.stream().anyMatch(existing -> existing.getId().equals(order.getId()));
    }
}
