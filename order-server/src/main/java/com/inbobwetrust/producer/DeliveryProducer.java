package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;
import lombok.SneakyThrows;

import java.net.URISyntaxException;

public interface DeliveryProducer {

    @SneakyThrows
    Delivery sendAddDeliveryMessage(Delivery delivery) throws URISyntaxException;

    void sendSetRiderMessage(Delivery updatedDelivery);
}
