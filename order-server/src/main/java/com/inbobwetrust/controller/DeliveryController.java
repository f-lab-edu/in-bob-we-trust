package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.DeliveryControllerSwaggerDoc;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.service.DeliveryService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryControllerSwaggerDoc {
  private final DeliveryService deliveryService;

  @PostMapping
  public Mono<Delivery> addDelivery(@RequestBody @Valid Delivery delivery) {
    return deliveryService.addDelivery(delivery);
  }

  @PutMapping("/accept")
  public Mono<Delivery> acceptDelivery(@RequestBody @Valid Delivery delivery) {
    return deliveryService.acceptDelivery(delivery);
  }

  @PutMapping("/rider")
  public Mono<Delivery> setDeliveryRider(@RequestBody @Valid Delivery delivery) {
    return deliveryService.setDeliveryRider(delivery);
  }

  @PutMapping("/pickup")
  public Mono<Delivery> setPickedUp(@RequestBody @Valid Delivery delivery) {
    return deliveryService.setPickedUp(delivery);
  }

  @PutMapping("/complete")
  public Mono<Delivery> setComplete(@RequestBody @Valid Delivery delivery) {
    return deliveryService.setComplete(delivery);
  }
}
