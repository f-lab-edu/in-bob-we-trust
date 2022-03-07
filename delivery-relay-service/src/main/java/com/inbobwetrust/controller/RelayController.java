package com.inbobwetrust.controller;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.ReceiverType;
import com.inbobwetrust.domain.RelayRequest;
import com.inbobwetrust.repository.RelayRepository;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/relay/v1")
@Slf4j
public class RelayController {

  private final RelayRepository relayRepository;

  @PostMapping("/shop/{shopId}")
  public Mono<Delivery> sendShopRequest(
      @PathVariable String shopId, @RequestBody @Valid Delivery delivery) {
    return relayRepository
        .save(new RelayRequest(ReceiverType.SHOP, shopId, delivery))
        .flatMap(relay -> Mono.just(relay.getDelivery()));
  }

  @PostMapping("/agency/{agencyId}")
  public Mono<Delivery> sendAgencyRequest(
      @PathVariable String agencyId, @RequestBody @Valid Delivery delivery) {
    return relayRepository
        .save(new RelayRequest(ReceiverType.AGENCY, agencyId, delivery))
        .flatMap(relay -> Mono.just(relay.getDelivery()));
  }
}
