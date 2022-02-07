package com.inbobwetrust.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

  String IP_PREFIX = "IP is...";
  String ipMessage = IP_PREFIX;

  @GetMapping("/ip")
  Mono<String> getIp() {
    if (ipMessage.equals(IP_PREFIX)) {
      try {
        InetAddress ip = InetAddress.getLocalHost();
        ipMessage += " From host: " + ip;
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }
    return Mono.just(ipMessage);
  }
}
