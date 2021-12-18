package com.inbobwetrust.repository;

import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.entity.DeliveryStatus;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {
    int save(Delivery delivery);

    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findAll();

    int update(Delivery delivery);

    Optional<DeliveryStatus> findDeliveryStatusByOrderId(Long orderId);
}
