package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Delivery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class TestDeliveryRepositoryImpl implements DeliveryRepository {
    private static final List<Delivery> deliveries = new ArrayList<>();

    @Override
    public boolean save(Delivery delivery) {
        if (contains(delivery)) return false;
        return deliveries.add(delivery);
    }

    @Override
    public Optional<Delivery> findByOrderId(String orderId) {
        return deliveries.stream()
                .filter(existing -> existing.getOrderId().equals(orderId))
                .findFirst();
    }

    @Override
    public List<Delivery> findAll() {
        return Collections.unmodifiableList(deliveries);
    }

    @Override
    public boolean update(Delivery delivery) {
        if (!contains(delivery)) return false;
        int index = findIdxByOrderId(delivery.getOrderId());
        deliveries.set(index, delivery);
        return true;
    }

    private int findIdxByOrderId(String orderId) {
        for (int i = 0; i < deliveries.size(); i++) {
            if (deliveries.get(i).getOrderId().equals(orderId)) {
                return i;
            }
        }
        throw new RuntimeException("Unexpected Output : missing delivery element");
    }

    private boolean contains(Delivery delivery) {
        return deliveries.stream()
                .anyMatch(existing -> existing.getOrderId().equals(delivery.getOrderId()));
    }

    public void clear() {
        while (!deliveries.isEmpty()) {
            deliveries.remove(0);
        }
    }
}
