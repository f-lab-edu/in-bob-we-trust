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

  @Override
  public Mono<Delivery> setDeliveryRider(Delivery delivery) {
    return deliveryRepository
        .findById(delivery.getId())
        .switchIfEmpty(Mono.error(DeliveryNotFoundException::new))
        .flatMap(DeliveryServiceValidator::canSetDeliveryRider)
        .flatMap(deliveryRepository::save);
  }

  public static final String MSG_RIDER_ALREADY_SET = "배정된 라이더가 존재합니다. 라이더ID : ";
  public static final String MSG_INVALID_STATUS_FOR_SETRIDER = "라이더배정가능한 상태가 아닙니다. 현재상태 : ";
  public static final String MSG_NULL_FINISHTIME = "배달완료시간이 설정되지 않았습니다.";

  private static class DeliveryServiceValidator {

    private static Mono<Delivery> canSetDeliveryRider(Delivery delivery) {
      StringBuilder errorMessage = new StringBuilder("");
      if (Objects.nonNull(delivery.getRiderId())) {
        errorMessage.append(MSG_RIDER_ALREADY_SET + delivery.getRiderId());
      }
      if (!delivery.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED)) {
        errorMessage.append(MSG_INVALID_STATUS_FOR_SETRIDER + delivery.getDeliveryStatus());
      }
      if (Objects.isNull(delivery.getFinishTime())) {
        errorMessage.append(MSG_NULL_FINISHTIME);
      }
      if (errorMessage.toString().isEmpty() || errorMessage.toString().isBlank()) {
        return Mono.just(delivery);
      }
      throw new IllegalArgumentException(errorMessage.toString());
    }
  }
}
