package com.inbobwetrust.producer.local.consumer;

import org.springframework.context.ApplicationEvent;

public interface MessageConsumer<T extends ApplicationEvent> {

    void handle(T event);
}
