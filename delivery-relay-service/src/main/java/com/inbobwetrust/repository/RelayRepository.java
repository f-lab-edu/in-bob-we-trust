package com.inbobwetrust.repository;

import com.inbobwetrust.domain.RelayRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RelayRepository extends ReactiveCrudRepository<RelayRequest, String> {}
