package com.inbobwetrust.producer;

import com.inbobwetrust.model.entity.Delivery;

public interface DeliveryProducer {

    Delivery sendAddDeliveryMessage(Delivery delivery);
}
