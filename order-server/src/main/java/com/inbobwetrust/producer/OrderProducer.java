package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Order;

public interface OrderProducer {
    Order sendNewOrderMessage(Order order);
}
