package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.producer.DeliveryProducer;
import com.inbobwetrust.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryProducer deliveryProducer;

    public Delivery addDelivery(Delivery delivery) {
        addEstimatedDeliveryFinishTime(delivery);
        saveOrThrow(delivery, "Save Operation Failed : delivery with such ID exists");
        Delivery savedDelivery = findByOrderId(delivery.getOrderId());
        deliveryProducer.sendAddDeliveryMessage(savedDelivery);
        return savedDelivery;
    }

    public void addEstimatedDeliveryFinishTime(Delivery delivery) {
        delivery.setEstimatedDeliveryFinishTime(delivery.getWantedPickupTime().plusMinutes(30));
    }

    public Delivery setRider(Delivery delivery) {
        validateSetRider(delivery);
        updateOrThrow(delivery, "setRider() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        deliveryProducer.sendSetRiderMessage(updatedDelivery);
        return updatedDelivery;
    }

    public Delivery setStatusPickup(Delivery delivery) {
        validateSetStatus(delivery);
        updateOrThrow(delivery, "setStatusPickup() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        deliveryProducer.sendSetStatusPickupMessage(updatedDelivery);
        return updatedDelivery;
    }

    private void saveOrThrow(Delivery delivery, String msg) {
        if (!deliveryRepository.save(delivery)) {
            throw new RuntimeException(msg);
        }
    public Delivery setStatusComplete(Delivery delivery) {
        validateSetStatus(delivery);
        updateOrThrow(delivery, "setStatusComplete() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        deliveryProducer.sendSetStatusCompleteMessage(updatedDelivery);
        return updatedDelivery;
    }

    private void updateOrThrow(Delivery delivery, String msg) {
        if (!deliveryRepository.update(delivery)) {
            throw new RuntimeException(msg);
        }
    }

    private void validateSetRider(Delivery delivery) {
        riderValidation(delivery.getRiderId());
        agentValidation(delivery.getDeliveryAgentId());
    }

    private void validateSetStatus(Delivery delivery) {
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
