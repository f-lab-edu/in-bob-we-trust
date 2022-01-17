package com.inbobwetrust.relay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class HealthCheckController {

  @Value("${hello.world}")
  private String helloWorld;

  @GetMapping
  Mono<String> helloWorld() {
    return Mono.just(helloWorld);
  }
}
