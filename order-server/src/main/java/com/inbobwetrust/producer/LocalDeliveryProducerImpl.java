package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;
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

    @Override
    public void sendSetRiderMessage(Delivery delivery) {
        applicationEventPublisher.publishEvent(new LocalSetRiderEvent(delivery));
    }

    @Override
    public void sendSetStatusPickupMessage(Delivery delivery) {
        applicationEventPublisher.publishEvent(new LocalSetStatusPickupEvent(delivery));
    }
}