package com.inbobwetrust.producer.local.consumer;

import com.inbobwetrust.producer.LocalNewOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("event.consumer.local")
@RequiredArgsConstructor
public class LocalNewOrderConsumer {
    private final NewOrderRelay orderRelay;

    @EventListener(classes = {LocalNewOrderEvent.class})
    public void handleNewOrderEvent(LocalNewOrderEvent event) {
        orderRelay.handle(event);
    }
}
