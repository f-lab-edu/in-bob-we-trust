package com.inbobwetrust.controller;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.service.DeliveryService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {
  private final DeliveryService deliveryService;

  @PostMapping
  Mono<Delivery> addDelivery(@RequestBody @Valid Delivery delivery) {
    return deliveryService.addDelivery(delivery);
  }

  @PutMapping("/accept")
  Mono<Delivery> acceptDelivery(@RequestBody @Valid Delivery delivery) {
    return deliveryService.acceptDelivery(delivery);
  }

  @PutMapping("/rider")
  Mono<Delivery> setDeliveryRider(@RequestBody @Valid Delivery delivery) {
    return deliveryService.setDeliveryRider(delivery);
  }
}
