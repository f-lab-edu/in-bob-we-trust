package com.inbobwetrust.producer.local.event;

import com.inbobwetrust.model.vo.Delivery;
import org.springframework.context.ApplicationEvent;

public class LocalSetStatusPickupEvent extends ApplicationEvent {
    public LocalSetStatusPickupEvent(Delivery delivery) {
        super(delivery);
    }

    @Override
    public Delivery getSource() {
        return (Delivery) super.getSource();
    }
}
