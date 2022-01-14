package com.inbobwetrust.domain;

import reactor.core.publisher.Mono;

public interface DeliveryRepository {

  Mono<Boolean> isPickedUp(String deliveryId);
}
