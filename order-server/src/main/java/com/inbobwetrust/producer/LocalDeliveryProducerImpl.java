package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.producer.DeliveryProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalDeliveryProducerImpl implements DeliveryProducer {
    private final ApplicationEventPublisher applicationEventPublisher;


    @Override
    public void sendAddDeliveryMessage(Delivery delivery) {
        applicationEventPublisher.publishEvent(new LocalAddDeliveryEvent(delivery));
    }
}
