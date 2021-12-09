package com.inbobwetrust.producer.local.consumer.adddelivery;

import com.inbobwetrust.producer.LocalAddDeliveryEvent;
import com.inbobwetrust.producer.LocalNewOrderEvent;
import com.inbobwetrust.producer.local.consumer.MessageConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("event.consumer.local")
@RequiredArgsConstructor
public class LocalAddDeliveryConsumer implements MessageConsumer<LocalAddDeliveryEvent> {
    private final AddDeliveryRelay deliveryRelay;

    @Override
    @EventListener(classes = {LocalAddDeliveryEvent.class})
    public void handle(LocalAddDeliveryEvent event) {
        deliveryRelay.handle(event);
    }
}
