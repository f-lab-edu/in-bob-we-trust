package com.inbobwetrust.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RiderLocationService {

  private final ReactiveRedisTemplate<String, RiderLocation> locationOperations;

  public Mono<Boolean> setIfPresent(String id, RiderLocation location) {
    return locationOperations.opsForValue().setIfPresent(id, location);
  }
}
