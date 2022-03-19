package com.inbobwetrust.subscriber;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.ReceiverType;
import com.inbobwetrust.domain.RelayRequest;
import com.inbobwetrust.repository.RelayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryMessageSubscriber {
  public static final String shopExchange = "messageQueue.exchange.shop";
  public static final String agencyExchange = "messageQueue.exchange.agency";

  private final RelayRepository relayRepository;

  @RabbitListener(
    ackMode = "MANUAL",
    id = "addDeliveryMessageListener",
    bindings = @QueueBinding(
      value = @Queue,
      exchange = @Exchange("messageQueue.exchange.shop"),
      key = "shop"
    ))
  public Mono<Void> processAddDeliveryMessage(Delivery delivery) {
    log.info("Consuming addDelivery     ===>      " + delivery);
    return relayRepository.save(new RelayRequest(ReceiverType.SHOP, delivery.getShopId(), delivery)).then();
  }

  @RabbitListener(
    ackMode = "MANUAL",
    id = "setRiderMessageListener",
    bindings = @QueueBinding(
      value = @Queue,
      exchange = @Exchange("messageQueue.exchange.agency"),
      key = "agency"
    ))
  public Mono<Void> processSetRiderMessage(Delivery delivery) {
    log.info("Consuming setRider      ===>      " + delivery);
    return relayRepository.save(new RelayRequest(ReceiverType.AGENCY, delivery.getAgencyId(), delivery)).then();
  }
}
