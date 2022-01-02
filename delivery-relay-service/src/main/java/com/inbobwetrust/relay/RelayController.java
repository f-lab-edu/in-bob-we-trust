package com.inbobwetrust.relay;

import com.inbobwetrust.relay.domain.Delivery;
import com.inbobwetrust.relay.domain.ReceiverType;
import com.inbobwetrust.relay.domain.RelayRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${restClient.proxy.baseUrl}")
@Slf4j
public class RelayController {

  private final RelayRepository relayRepository;

  @PostMapping("/shop/{shopId}")
  public Mono<Delivery> sendShopRequest(
      @PathVariable String shopId, @RequestBody @Valid Delivery delivery) {
    return relayRepository
        .save(new RelayRequest(ReceiverType.SHOP, shopId, delivery))
        .flatMap(relay -> Mono.just(relay.getDelivery()))
        .log();
  }

  @PostMapping("/agency/{agencyId}")
  public Mono<Delivery> sendAgencyRequest(
      @PathVariable String agencyId, @RequestBody @Valid Delivery delivery) {
    return relayRepository
        .save(new RelayRequest(ReceiverType.AGENCY, agencyId, delivery))
        .flatMap(relay -> Mono.just(relay.getDelivery()))
        .log();
  }
}
