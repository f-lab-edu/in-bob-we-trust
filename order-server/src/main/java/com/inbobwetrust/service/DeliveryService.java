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
        if (!deliveryRepository.save(delivery)) {
            throw new RuntimeException("Save Operation Failed : delivery with such ID exists");
        }
        Optional<Delivery> savedDelivery = deliveryRepository.findByOrderId(delivery.getOrderId());
        if (savedDelivery.isEmpty()) {
            throw new RuntimeException("Cannot find saved Delivery");
        }
        deliveryProducer.sendAddDeliveryMessage(savedDelivery.get());
        return savedDelivery.get();
    }

    public void addEstimatedDeliveryFinishTime(Delivery delivery) {
        delivery.setEstimatedDeliveryFinishTime(delivery.getWantedPickupTime().plusMinutes(30));
    }
}
