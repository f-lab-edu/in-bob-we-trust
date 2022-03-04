package com.inbobwetrust.domain;

import com.inbobwetrust.repository.DeliveryRepository;
import com.inbobwetrust.repository.RiderLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RiderLocationService {
  private final RiderLocationRepository repository;
  private final DeliveryRepository deliveryRepository;

  public Mono<Boolean> tryPutOperation(RiderLocation location) {
    return repository.setIfPresent(location).flatMap(isSaved -> orElseSetNew(isSaved, location));
  }


  private Mono<Boolean> orElseSetNew(Boolean isSaved, RiderLocation location) {
    return isSaved ? Mono.just(true) : doSetNew(location);
  }

  private Mono<Boolean> doSetNew(RiderLocation location) {
    return deliveryRepository
        .isPickedUp(location.getDeliveryId())
        .flatMap(isPickedUp -> setIfPickedUp(isPickedUp, location));
  }

  private Mono<Boolean> setIfPickedUp(Boolean isPickedUp, RiderLocation location) {
    return isPickedUp ? repository.setIfAbsent(location) : Mono.just(false);
  }


  public Flux<RiderLocation> findAll() {
    return repository.findAll();
  }

  public Mono<RiderLocation> getLocation(String deliveryId) {
    return repository.getLocation(deliveryId);
  }
}
