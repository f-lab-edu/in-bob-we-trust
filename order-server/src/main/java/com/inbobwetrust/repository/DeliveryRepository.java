package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.DeliveryStatus;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {
    boolean save(Delivery delivery);

    Optional<Delivery> findByOrderId(String orderId);

    List<Delivery> findAll();

    boolean update(Delivery delivery);

    Optional<DeliveryStatus> findDeliveryStatusByOrderId(String orderId);
}
