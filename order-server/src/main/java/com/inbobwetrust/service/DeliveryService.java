package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.producer.DeliveryProducer;
import com.inbobwetrust.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryProducer deliveryProducer;

    public Delivery setRider(Delivery delivery) {
        validateSetRider(delivery);
        if (!deliveryRepository.update(delivery)) {
            throw new RuntimeException("setRider Operation Failed : No Such OrderId");
        }
        Optional<Delivery> updatedDelivery =
                deliveryRepository.findByOrderId(delivery.getOrderId());
        if (updatedDelivery.isEmpty()) {
            throw new RuntimeException("Cannot find updated Delivery");
        }
        deliveryProducer.sendSetRiderMessage(updatedDelivery.get());
        return updatedDelivery.get();
    }

    private void validateSetRider(Delivery delivery) {
        riderValidation(delivery);
        agentValidation(delivery);
    }

    private void riderValidation(Delivery delivery) {
        if (delivery.getRiderId() == null
                || delivery.getRiderId().isEmpty()
                || delivery.getRiderId().isBlank()) {
            throw new IllegalArgumentException("Rider not set for delivery");
        }
    }

    private void agentValidation(Delivery delivery) {
        if (delivery.getDeliveryAgentId() == null
                || delivery.getDeliveryAgentId().isBlank()
                || delivery.getDeliveryAgentId().isEmpty()) {
            throw new IllegalArgumentException("deliveryAgent not set for delivery");
        }
    }
}
