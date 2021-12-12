package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.Order;

public interface EndpointService {
    String findShopEndpoint(Order order);

    String findDeliveryAgentEndpoint(Delivery delivery);
}
