package com.inbobwetrust.service;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.exception.RetryExhaustedException;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public interface DeliveryService {
  Mono<Delivery> addDelivery(Delivery delivery);

  Mono<Delivery> acceptDelivery(Delivery delivery);

  Mono<Delivery> setDeliveryRider(Delivery delivery);

  Mono<Delivery> setPickedUp(Delivery delivery);

  Mono<Delivery> setComplete(Delivery delivery);

  Mono<Delivery> findById(String id);

  Flux<Delivery> findAll(PageRequest pageable);

  Long MAX_ATTEMPTS = 3L;
  Duration FIXED_DELAY = Duration.ofMillis(500);

  default RetryBackoffSpec defaultRetryBackoffSpec() {
    return Retry.fixedDelay(MAX_ATTEMPTS, FIXED_DELAY)
        .filter(
            (ex) -> {
              if (ex instanceof TimeoutException) {
                return true;
              }
              return false;
            })
        .onRetryExhaustedThrow(
            ((retryBackoffSpec, retrySignal) -> new RetryExhaustedException(retrySignal)));
  }
}
