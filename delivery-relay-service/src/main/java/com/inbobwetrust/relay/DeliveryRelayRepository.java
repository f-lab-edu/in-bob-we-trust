package com.inbobwetrust.relay;

import com.inbobwetrust.relay.domain.RelayRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DeliveryRelayRepository extends ReactiveCrudRepository<RelayRequest, String> {}
