package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    public Delivery addDelivery(Delivery delivery) {
        return delivery;
    }
}
