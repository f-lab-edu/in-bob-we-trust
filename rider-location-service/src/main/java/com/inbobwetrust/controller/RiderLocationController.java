package com.inbobwetrust.controller;

import com.inbobwetrust.domain.RiderLocation;
import com.inbobwetrust.service.RiderLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/rider/location")
@RequiredArgsConstructor
public class RiderLocationController {
  private final RiderLocationService locationService;

  @PutMapping
  public Mono<Boolean> updateLocationPost(@RequestBody RiderLocation location) {
    return locationService.tryPutOperation(location);
  }

  @PostMapping
  public Mono<Boolean> updateLocationPut(@RequestBody RiderLocation location) {
    return locationService.tryPutOperation(location);
  }

  @GetMapping
  public Mono<RiderLocation> getLocation(@RequestParam(name = "deliveryId") String deliveryId) {
    return locationService.getLocation(deliveryId);
  }
}
