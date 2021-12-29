package com.inbobwetrust.service;

import com.inbobwetrust.domain.Delivery;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeliveryService {
  Mono<Delivery> addDelivery(Delivery delivery);

  Mono<Delivery> acceptDelivery(Delivery delivery);

  Mono<Delivery> setDeliveryRider(Delivery delivery);

  Mono<Delivery> setPickedUp(Delivery delivery);

  Mono<Delivery> setComplete(Delivery delivery);

  Mono<Delivery> findById(String id);

  Flux<Delivery> findAll(PageRequest pageable);
}
