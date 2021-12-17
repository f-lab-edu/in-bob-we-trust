package com.inbobwetrust.service;

import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.entity.Order;

import org.springframework.stereotype.Component;

public interface EndpointService {
    String findShopEndpoint(Order order);

    String findDeliveryAgentEndpoint(Delivery delivery);
}

@Component
class EndpointServiceTestImpl implements EndpointService {

    @Override
    public String findShopEndpoint(Order order) {
        return null;
    }

    @Override
    public String findDeliveryAgentEndpoint(Delivery delivery) {
        return null;
    }
}
