package com.inbobwetrust.producer.local.consumer.neworder;

import com.inbobwetrust.producer.LocalNewOrderEvent;
import com.inbobwetrust.producer.local.consumer.ExternalMessageConsumer;
import org.springframework.stereotype.Component;

@Component
public class NewOrderRelay {
    private final ExternalMessageConsumer shopConnections = new Shops();

    public void handle(LocalNewOrderEvent event) {
        shopConnections.receive(event.getSource());
    }
}
