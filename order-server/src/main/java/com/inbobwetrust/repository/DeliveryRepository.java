package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.DeliveryStatus;

import java.util.Optional;

public interface DeliveryRepository {
    Optional<DeliveryStatus> findDeliveryStatusByOrderId(String orderId);
}
