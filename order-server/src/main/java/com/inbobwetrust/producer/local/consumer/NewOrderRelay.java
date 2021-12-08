package com.inbobwetrust.producer.local.consumer;

import com.inbobwetrust.producer.LocalNewOrderEvent;
import org.springframework.stereotype.Component;

@Component
public class NewOrderRelay {
    private final Shops shopConnections = new Shops();

    public void handle(LocalNewOrderEvent event) {
        shopConnections.send(event.getSource());
    }
}
