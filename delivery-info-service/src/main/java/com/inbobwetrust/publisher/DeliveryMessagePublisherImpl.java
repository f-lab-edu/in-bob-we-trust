package com.inbobwetrust.publisher;

import com.inbobwetrust.domain.Delivery;
import org.springframework.amqp.core.AmqpTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class DeliveryMessagePublisherImpl implements DeliveryPublisher {

  private final AmqpTemplate messageQueue;

  private String shopExchange;

  private String agencyExchange;

  public DeliveryMessagePublisherImpl(AmqpTemplate messageQueue, String shopExchange, String agencyExchange) {
    this.messageQueue = messageQueue;
    this.shopExchange = shopExchange;
    this.agencyExchange = agencyExchange;
  }

  @Override
  public Mono<Delivery> sendAddDeliveryEvent(Delivery delivery) {
    return Mono.just(delivery).subscribeOn(Schedulers.boundedElastic())
      .flatMap(this::publishAddDeliveryEvent);
  }

  private Mono<Delivery> publishAddDeliveryEvent(Delivery delivery) {
    return Mono.fromCallable(() -> {
      this.messageQueue.convertAndSend(shopExchange, delivery);
      return delivery;
    });
  }

  @Override
  public Mono<Delivery> sendSetRiderEvent(Delivery delivery) {
    return Mono.just(delivery)
      .subscribeOn(Schedulers.boundedElastic()).flatMap(
        this::publishSetRiderEvent
      );
  }

  private Mono<Delivery> publishSetRiderEvent(Delivery delivery) {
    return Mono.fromCallable(() -> {
      this.messageQueue.convertAndSend(agencyExchange, delivery);
      return delivery;
    });
  }
}
