package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;

public interface DeliveryProducer {

    Delivery sendAddDeliveryMessage(Delivery delivery);

    void sendSetRiderMessage(Delivery updatedDelivery);
}
