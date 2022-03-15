package com.inbobwetrust.publisher;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.exception.RelayClientException;
import com.inbobwetrust.exception.RelayServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryPublisherImpl implements DeliveryPublisher {
  private final WebClient webClient;

  @Value("${restClient.proxy.shopUrl}")
  private String proxyShopUrl;

  @Value("${restClient.proxy.agencyUrl}")
  private String proxyAgencyUrl;

  private final AmqpTemplate amqpTemplate;

  public Mono<Delivery> sendAddDeliveryEvent(Delivery delivery) {
    return Mono.just(delivery)
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap(this::publishAddDeliveryEvent);
  }

  @Override
  public Mono<Delivery> sendSetRiderEvent(Delivery delivery) {
    return Mono.just(delivery)
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap(this::publishSetRiderEvent);
  }


  private Mono<Delivery> publishAddDeliveryEvent(Delivery delivery) {
    return Mono.fromCallable(
      () -> {
        this.amqpTemplate.convertAndSend(
          "inbobwetrust-delivery-addDelivery", "new-items-spring-amqp", delivery);
        return delivery;
      });
  }

  private Mono<Delivery> publishSetRiderEvent(Delivery delivery) {
    return Mono.fromCallable(
      () -> {
        this.amqpTemplate.convertAndSend(
          "inbobwetrust-delivery-setRider", "new-items-spring-amqp", delivery);
        return delivery;
      });
  }


  private Mono<? extends Throwable> handleOn5xxStatus(
    Delivery delivery, ClientResponse serverResponse) {
    log.info("Status code 5XX is : {}", serverResponse.statusCode().value());
    log.info("Error Status 5XX for delivery : {}", delivery);
    return Mono.error(
      new RelayServerException("Shop operation failed for delivery :     " + delivery));
  }

  private Mono<? extends Throwable> handleOn4xxStatus(
    Delivery delivery, ClientResponse clientResponse) {
    log.info("Status code 4xx is : {}", clientResponse.statusCode().value());
    log.info("Error Status 4XX for delivery : {}", delivery);
    return Mono.error(new RelayClientException("Push Event failed for delivery :     " + delivery));
  }
}
