package com.inbobwetrust.producer.local.consumer;

public interface ExternalMessageConsumer<T> {
    void receive(T message);
}
