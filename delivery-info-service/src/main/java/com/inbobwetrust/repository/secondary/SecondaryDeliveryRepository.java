package com.inbobwetrust.repository.secondary;

import com.inbobwetrust.domain.Delivery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface SecondaryDeliveryRepository extends ReactiveMongoRepository<Delivery, String> {
  Flux<Delivery> findAllByOrderIdContaining(String id, Pageable pageable);
}
