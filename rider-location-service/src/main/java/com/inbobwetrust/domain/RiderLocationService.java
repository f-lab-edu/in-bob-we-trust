package com.inbobwetrust.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RiderLocationService {

  private final RiderLocationRepository repository;

  public Mono<Boolean> setIfPresent(RiderLocation location) {
    return repository.setIfPresent(location);
  }
}
