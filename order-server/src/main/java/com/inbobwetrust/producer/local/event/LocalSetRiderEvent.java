package com.inbobwetrust.producer.local.event;

import com.inbobwetrust.model.vo.Delivery;
import org.springframework.context.ApplicationEvent;

public class LocalSetRiderEvent extends ApplicationEvent {
    public LocalSetRiderEvent(Delivery delivery) {
        super(delivery);
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }
}
