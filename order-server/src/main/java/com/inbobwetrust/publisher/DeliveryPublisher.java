package com.inbobwetrust.publisher;

import com.inbobwetrust.domain.Delivery;
import reactor.core.publisher.Mono;

public interface DeliveryPublisher {

  void sendAddDeliveryEvent(Delivery delivery);
}
