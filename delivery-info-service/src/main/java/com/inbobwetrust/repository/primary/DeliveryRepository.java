package com.inbobwetrust.repository.primary;

import com.inbobwetrust.domain.Delivery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface DeliveryRepository extends ReactiveMongoRepository<Delivery, String> {
  Flux<Delivery> findAllByOrderIdContaining(String id, Pageable pageable);
}
