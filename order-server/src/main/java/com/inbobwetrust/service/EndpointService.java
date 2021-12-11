package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.Order;
import org.reactivestreams.Publisher;

public interface EndpointService {
    String findShopEndpoint(Order order);

    String findDeliveryAgentEndpoint(Delivery delivery);
}
