package com.inbobwetrust.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class DeliveryRepositoryImpl implements DeliveryRepository {

  @Value("${services.deliveryInfoService.path}")
  private String uriDeliveryInfoService;

  private final WebClient webClient;

  public DeliveryRepositoryImpl() {
    this.webClient = WebClient.builder().build();
  }

  public void setUriDeliveryInfoService(String uriDeliveryInfoService) {
    this.uriDeliveryInfoService = uriDeliveryInfoService;
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
