package com.inbobwetrust.repository;

import com.inbobwetrust.model.dto.DeliveryStatusDto;
import com.inbobwetrust.model.entity.Delivery;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {
    boolean save(Delivery delivery);

    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findAll();

    boolean update(Delivery delivery);

    Optional<DeliveryStatusDto> findDeliveryStatusByOrderId(Long orderId);

    Optional<Delivery> findById(Long orderId);
}
