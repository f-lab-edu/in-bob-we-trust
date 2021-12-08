package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Order;
import org.springframework.context.ApplicationEvent;

public class LocalNewOrderEvent extends ApplicationEvent {
    public LocalNewOrderEvent(Order order) {
        super(order);
    }

    @Override
    public Order getSource() {
        return (Order) super.getSource();
    }
}
