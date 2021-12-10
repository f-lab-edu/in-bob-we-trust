package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Order;

public interface OrderProducer {
    void sendNewOrderMessage(Order order);
}
