package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public DeliveryStatus findDeliveryStatusByOrderId(String orderId) {
        Optional<DeliveryStatus> result = deliveryRepository.findDeliveryStatusByOrderId(orderId);
        if (result.isPresent()) {
            return result.get();
        }
        throw new RuntimeException("No such delivery associated with Id");
    }
}
