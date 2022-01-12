package com.inbobwetrust.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RiderLocationRepository {
  private final ReactiveRedisTemplate<String, RiderLocation> locationOperations;

  public Mono<Boolean> setIfPresent(RiderLocation location) {
    return locationOperations.opsForValue().setIfPresent(location.getId(), location);
  }
}
