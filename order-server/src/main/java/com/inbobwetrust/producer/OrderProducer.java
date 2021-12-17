package com.inbobwetrust.producer;

import com.inbobwetrust.model.entity.Order;

public interface OrderProducer {
    Order sendNewOrderMessage(Order order);
}
