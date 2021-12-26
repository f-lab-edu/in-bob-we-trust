package com.inbobwetrust.service;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.DeliveryNotFoundException;
import com.inbobwetrust.publisher.DeliveryPublisher;
import com.inbobwetrust.repository.DeliveryRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

  private final DeliveryRepository deliveryRepository;
  private final DeliveryPublisher deliveryPublisher;

  @Override
  public Mono<Delivery> addDelivery(Delivery delivery) {
    return deliveryRepository.save(delivery).flatMap(deliveryPublisher::sendAddDeliveryEvent);
  }

  @Override
  public Mono<Delivery> acceptDelivery(Delivery delivery) {
    try {
      invalidAcceptDelivery(delivery);
    } catch (IllegalStateException se) {
      return Mono.error(IllegalStateException::new);
    }

    return deliveryRepository
        .findById(delivery.getId())
        .switchIfEmpty(Mono.error(DeliveryNotFoundException::new))
        .flatMap(deliveryRepository::save)
        .flatMap(deliveryPublisher::sendSetRiderEvent);
  }

  @Override
  public Mono<Delivery> setDeliveryRider(Delivery delivery) {
    return null;
  }

  private boolean invalidAcceptDelivery(Delivery delivery) {
    StringBuilder errorMessage = new StringBuilder("");
    errorMessage.append(Objects.nonNull(delivery.getDeliveryStatus()) ? "" : "주문상태가 null 입니다.");

    if (!delivery.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED)) {
      errorMessage.append("주문상태가 ACCEPTED가 아닙니다");
    }
    if (delivery.getPickupTime().isBefore(delivery.getOrderTime())) {
      errorMessage.append("픽업시간은 주문시간 이후여야 합니다.");
    }
    if (errorMessage.toString().isEmpty() || errorMessage.toString().isBlank()) {
      return true;
    }
    throw new IllegalArgumentException(errorMessage.toString());
  }
}
