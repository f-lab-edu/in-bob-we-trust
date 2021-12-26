package com.inbobwetrust.service;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.DeliveryNotFoundException;
import com.inbobwetrust.publisher.DeliveryPublisher;
import com.inbobwetrust.repository.DeliveryRepository;
import java.util.Objects;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.inbobwetrust.domain.DeliveryStatus.ACCEPTED;
import static com.inbobwetrust.domain.DeliveryStatus.PICKED_UP;

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
        .flatMap(DeliveryValidator::canSetDeliveryRider)
        .flatMap(deliveryRepository::save);
  }

  @Override
  public Mono<Delivery> setPickedUp(Delivery delivery) {
    return updateDeliveryWithBiFuncValidator(delivery, DeliveryValidator::canSetPickUp);
  }

  @Override
  public Mono<Delivery> setComplete(Delivery delivery) {
    return updateDeliveryWithBiFuncValidator(delivery, DeliveryValidator::canSetComplete);
  }

  private Mono<Delivery> updateDeliveryWithBiFuncValidator(
      Delivery newDelivery, BiFunction<Delivery, Delivery, Mono<Delivery>> validateFunc) {
    return deliveryRepository
        .findById(newDelivery.getId())
        .switchIfEmpty(Mono.error(DeliveryNotFoundException::new))
        .flatMap(oldDelivery -> validateFunc.apply(oldDelivery, newDelivery))
        .flatMap(deliveryRepository::save);
  }

  public static final String MSG_RIDER_ALREADY_SET = "배정된 라이더가 존재합니다. 라이더ID : ";
  public static final String MSG_INVALID_STATUS_FOR_SETRIDER = "라이더배정가능한 상태가 아닙니다. 현재상태 : ";
  public static final String MSG_NULL_FINISHTIME = "배달완료시간이 설정되지 않았습니다.";
  public static final String MSG_INVALID_STATUS_FOR_UPDATE = "기존상태와 호환이 되지 않습니다. 현재상태 : ";

  private static class DeliveryValidator {

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
      return monoJustOrError(delivery, errorMessage);
    }

    public static Mono<Delivery> canSetPickUp(Delivery before, Delivery after) {
      assert Objects.nonNull(before) && Objects.nonNull(after);
      var errorMessage = new StringBuilder("");

      if (!canUpdateStatus(ACCEPTED, before, after)) {
        errorMessage.append(MSG_INVALID_STATUS_FOR_UPDATE + before.getDeliveryStatus());
      }
      return monoJustOrError(after, errorMessage);
    }

    public static Mono<Delivery> canSetComplete(Delivery before, Delivery after) {
      assert Objects.nonNull(before) && Objects.nonNull(after);
      StringBuilder errorMessage = new StringBuilder("");

      if (!canUpdateStatus(PICKED_UP, before, after)) {
        errorMessage.append(MSG_INVALID_STATUS_FOR_UPDATE + before.getDeliveryStatus());
      }
      return monoJustOrError(after, errorMessage);
    }

    private static boolean canUpdateStatus(
        DeliveryStatus expectedBeforeStatus, Delivery before, Delivery after) {
      return before.getDeliveryStatus().equals(expectedBeforeStatus)
          && before.getDeliveryStatus().canProceedTo(after.getDeliveryStatus());
    }

    private static Mono<Delivery> monoJustOrError(Delivery after, StringBuilder errorMessage) {
      if (errorMessage.toString().isEmpty() || errorMessage.toString().isBlank()) {
        return Mono.just(after);
      }
      return Mono.error(new IllegalArgumentException(errorMessage.toString()));
    }
  }
}
