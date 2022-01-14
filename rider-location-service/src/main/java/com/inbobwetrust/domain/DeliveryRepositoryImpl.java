package com.inbobwetrust.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class DeliveryRepositoryImpl implements DeliveryRepository {

  @Value("${services.deliveryInfoService.path}")
  private String uriDeliveryInfoService;

  private final WebClient webClient;

  public DeliveryRepositoryImpl() {
    this.webClient = WebClient.builder().build();
  }

  @Override
  public Mono<Boolean> isPickedUp(String deliveryId) {
    return webClient
        .get()
        .uri(uriDeliveryInfoService + "/is-picked-up/" + deliveryId)
        .retrieve()
        .bodyToMono(Boolean.class);
  }
}
