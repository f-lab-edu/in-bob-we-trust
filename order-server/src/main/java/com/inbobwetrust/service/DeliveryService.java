package com.inbobwetrust.service;

import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.dto.DeliveryStatusDto;
import com.inbobwetrust.producer.DeliveryProducer;
import com.inbobwetrust.repository.DeliveryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

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
        delivery.setFinishTime(delivery.getPickupTime().plusMinutes(30));
    }

    public Delivery setRider(Delivery delivery) {
        updateOrThrow(delivery, "setRider() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        return updatedDelivery;
    }

    public Delivery setStatusPickup(Delivery delivery) {
        updateOrThrow(delivery, "setStatusPickup() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        return updatedDelivery;
    }

    public Delivery setStatusComplete(Delivery delivery) {
        updateOrThrow(delivery, "setStatusComplete() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        return updatedDelivery;
    }

    public DeliveryStatusDto findDeliveryStatusByOrderId(Long orderId) {
        return deliveryRepository
                .findDeliveryStatusByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No such delivery associated with Id"));
    }

    private void saveOrThrow(Delivery delivery, String msg) {
        if (!deliveryRepository.save(delivery)) {
            throw new RuntimeException(msg);
        }
    }

    private void updateOrThrow(Delivery delivery, String msg) {
        if (!deliveryRepository.update(delivery)) {
            throw new RuntimeException(msg);
        }
    }

    private Delivery findByOrderId(Long orderId) {
        return deliveryRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Cannot find Delivery"));
    }
}
