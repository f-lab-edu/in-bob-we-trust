package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Delivery;

import java.util.Optional;

public interface DeliveryRepository {
    boolean save(Delivery deliveryRequest);

    Optional<Delivery> findByOrderId(String orderId);
}
