package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;
import org.springframework.context.ApplicationEvent;

public class LocalAddDeliveryEvent extends ApplicationEvent {
    public LocalAddDeliveryEvent(Delivery delivery) {
        super(delivery);
    }

    @Override
    public Delivery getSource() {
        return (Delivery) super.getSource();
    }
}
