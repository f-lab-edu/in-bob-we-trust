package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.DeliveryControllerSwaggerDoc;
import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Slf4j
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

  public static final int DEFAULT_PAGE = 0;
  public static final int MAX_PAGE = Integer.MAX_VALUE;
  public static final int MIN_PAGE = 0;

  public static final int DEFAULT_SIZE = 10;
  public static final int MAX_SIZE = Integer.MAX_VALUE;
  public static final int MIN_SIZE = 1;

  @GetMapping
  public Flux<Delivery> getDeliveries(
      @RequestParam(required = false, name = "page") Integer page,
      @RequestParam(required = false, name = "size") Integer size) {
    page = Objects.isNull(page) || page < MIN_PAGE ? MIN_PAGE : page;
    size = Objects.isNull(size) || size < MIN_SIZE ? MIN_SIZE : size;
    return deliveryService.findAll(PageRequest.of(page, size));
  }

  @GetMapping("/{id}")
  public Mono<Delivery> getDelivery(@PathVariable @NotBlank(message = "배달번호가 비어있습니다.") String id) {
    return deliveryService.findById(id);
  }
}
