package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public Delivery updateDeliveryStatusPickup(Delivery delivery) {
        validateSetStatusToPickup(delivery);
        updateOrThrow(delivery, "setStatusToPickup() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        return updatedDelivery;
    }

    private void updateOrThrow(Delivery delivery, String msg) {
        if (!deliveryRepository.update(delivery)) {
            throw new RuntimeException(msg);
        }
    }

    private void validateSetStatusToPickup(Delivery delivery) {
        riderValidation(delivery.getRiderId());
        agentValidation(delivery.getDeliveryAgentId());
        statusValidation(delivery.getStatus());
    }

    private Delivery findByOrderId(String orderId) {
        Optional<Delivery> updatedDelivery = deliveryRepository.findByOrderId(orderId);
        if (updatedDelivery.isEmpty()) {
            throw new RuntimeException("Cannot find Delivery");
        }
        return updatedDelivery.get();
    }

    private void riderValidation(String rider) {
        validateStringOrThrow(rider, "Rider not set for delivery");
    }

    private void agentValidation(String agent) {
        validateStringOrThrow(agent, "deliveryAgent not set for delivery");
    }

    private void statusValidation(String status) {
        validateStringOrThrow(status, "status not set for delivery");
    }

    private void validateStringOrThrow(String str, String msg) {
        if (str == null || str.isBlank() || str.isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
    }
}
