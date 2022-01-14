package com.inbobwetrust.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RiderLocationService {

  private final RiderLocationRepository repository;
  private final DeliveryRepository deliveryRepository;

  public Mono<Boolean> setIfPresent(RiderLocation location) {
    return repository
        .setIfPresent(location)
        .flatMap(isSaved -> returnElseRegisterNew(isSaved, location));
  }

  private Mono<Boolean> returnElseRegisterNew(Boolean isSaved, RiderLocation location) {
    return isSaved ? Mono.just(true) : doRegisterNew(location);
  }

  private Mono<Boolean> doRegisterNew(RiderLocation location) {
    return deliveryRepository
        .isPickedUp(location.getDeliveryId())
        .flatMap(isPickedUp -> setIfPickedUpOrReturnFalse(isPickedUp, location));
  }

  private Mono<Boolean> setIfPickedUpOrReturnFalse(Boolean isPickedUp, RiderLocation location) {
    return isPickedUp ? repository.setIfAbsent(location) : Mono.just(Boolean.FALSE);
  }

  public Flux<RiderLocation> findAll() {
    return repository.findAll();
  }
}
