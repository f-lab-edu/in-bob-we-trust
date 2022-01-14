package com.inbobwetrust.domain;

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
    return locationService.setIfPresent(location);
  }

  @PostMapping
  public Mono<Boolean> updateLocationPut(@RequestBody RiderLocation location) {
    return locationService.setIfPresent(location);
  }
}
