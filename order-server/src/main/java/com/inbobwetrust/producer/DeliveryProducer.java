package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;

import java.net.URISyntaxException;

public interface DeliveryProducer<T> {
    T sendAddDeliveryMessage(Delivery delivery) throws URISyntaxException;
}
