package com.inbobwetrust.publisher;

import com.inbobwetrust.domain.Delivery;
import reactor.core.publisher.Mono;

public interface DeliveryPublisher {

  Mono<Delivery> sendAddDeliveryEvent(Delivery delivery);

  Mono<Delivery> sendSetRiderEvent(Delivery delivery);
}
