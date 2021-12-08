package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalOrderProducerImpl implements OrderProducer {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void sendNewOrderMessage(Order order) {
        applicationEventPublisher.publishEvent(new LocalNewOrderEvent(order));
    }
}
