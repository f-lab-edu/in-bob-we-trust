package com.inbobwetrust.repository;

import com.inbobwetrust.domain.Delivery;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DeliveryRepository extends ReactiveMongoRepository<Delivery, String> {}
