package com.inbobwetrust.repository;

import com.inbobwetrust.domain.RiderLocation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RiderLocationRepository {

  Mono<Boolean> setIfAbsent(RiderLocation location);

  Mono<Boolean> setIfPresent(RiderLocation location);

  Flux<RiderLocation> findAll();

  Flux<Boolean> deleteAll();

  Mono<RiderLocation> getLocation(String deliveryId);
}
