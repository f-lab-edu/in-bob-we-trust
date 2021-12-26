package com.inbobwetrust.service;

import com.inbobwetrust.domain.Delivery;
import reactor.core.publisher.Mono;

public interface DeliveryService {
  Mono<Delivery> addDelivery(Delivery delivery);

  Mono<Delivery> acceptDelivery(Delivery delivery);

  Mono<Delivery> setDeliveryRider(Delivery delivery);
}
