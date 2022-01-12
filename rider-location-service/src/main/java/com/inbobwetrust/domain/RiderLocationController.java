package com.inbobwetrust.domain;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rider/location")
public class RiderLocationController {
  private final RiderLocationService locationService;

  @PutMapping()
  public Mono<Boolean> updateLocation(@RequestBody RiderLocation location) {
    return locationService.setIfPresent(location);
  }
}
