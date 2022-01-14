package com.inbobwetrust.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RiderLocationRepository {

  Mono<Boolean> setIfAbsent(RiderLocation location);

  Mono<Boolean> setIfPresent(RiderLocation location);

  Flux<RiderLocation> findAll();

  Flux<Boolean> deleteAll();
}
