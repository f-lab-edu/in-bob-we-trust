package com.inbobwetrust.repository;

import com.inbobwetrust.domain.Delivery;
import io.netty.util.internal.logging.InternalLogger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface DeliveryRepository extends ReactiveCrudRepository<Delivery, String> {
  Flux<Delivery> findByOrderIdContaining(String id, Pageable pageable);
}
