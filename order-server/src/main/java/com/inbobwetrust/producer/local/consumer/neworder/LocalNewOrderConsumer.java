package com.inbobwetrust.producer.local.consumer.neworder;

import com.inbobwetrust.producer.LocalNewOrderEvent;
import com.inbobwetrust.producer.local.consumer.MessageConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("event.consumer.local")
@RequiredArgsConstructor
public class LocalNewOrderConsumer implements MessageConsumer<LocalNewOrderEvent> {
    private final NewOrderRelay orderRelay;

    public void handleNewOrderEvent(LocalNewOrderEvent event) {}

    @Override
    @EventListener(classes = {LocalNewOrderEvent.class})
    public void handle(LocalNewOrderEvent event) {
        orderRelay.handle(event);
    }
}
