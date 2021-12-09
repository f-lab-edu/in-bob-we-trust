package com.inbobwetrust.producer.local.consumer.adddelivery;

import com.inbobwetrust.producer.LocalAddDeliveryEvent;
import com.inbobwetrust.producer.LocalNewOrderEvent;
import com.inbobwetrust.producer.local.consumer.ExternalMessageConsumer;
import org.springframework.stereotype.Component;

@Component
public class AddDeliveryRelay {
    private final ExternalMessageConsumer deliveryAgents = new DeliveryAgents();

    public void handle(LocalAddDeliveryEvent event) {
        deliveryAgents.receive(event.getSource());
    }
}
