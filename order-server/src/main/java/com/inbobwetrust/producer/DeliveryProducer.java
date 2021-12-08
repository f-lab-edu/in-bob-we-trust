package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;

public interface DeliveryProducer {
    void sendAddDeliveryMessage(Delivery delivery);
}
