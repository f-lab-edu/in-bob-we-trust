package com.inbobwetrust.service;

import static com.inbobwetrust.domain.DeliveryStatus.ACCEPTED;
import static com.inbobwetrust.domain.DeliveryStatus.PICKED_UP;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;
import com.inbobwetrust.exception.DeliveryNotFoundException;
import com.inbobwetrust.publisher.DeliveryPublisher;
import com.inbobwetrust.repository.DeliveryRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
  private final DeliveryRepository deliveryRepository;
  private final DeliveryPublisher deliveryPublisher;

  @Override
  public Mono<Delivery> addDelivery(Delivery delivery) {
    return deliveryRepository
        .save(delivery)
        .timeout(FIXED_DELAY)
        .retryWhen(defaultRetryBackoffSpec())
        .flatMap(deliveryPublisher::sendAddDeliveryEvent)
        .log();
  }

  @Override
  public Mono<Delivery> acceptDelivery(Delivery delivery) {
    return Mono.just(delivery)
        .flatMap(DeliveryValidator::statusIsNotNull)
        .flatMap(DeliveryValidator::statusIsAccepted)
        .flatMap(DeliveryValidator::pickupTimeIsAfterOrderTime)
        .flatMap(del -> deliveryRepository.findById(del.getId()))
        .switchIfEmpty(Mono.error(DeliveryNotFoundException::new))
        .flatMap(deliveryRepository::save)
        .flatMap(deliveryPublisher::sendSetRiderEvent);
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
  public Mono<Delivery> setPickedUp(Delivery newDelivery) {
    return deliveryRepository
        .findById(newDelivery.getId())
        .switchIfEmpty(Mono.error(DeliveryNotFoundException::new))
        .flatMap(existingDelivery -> DeliveryValidator.canSetPickUp(existingDelivery, newDelivery))
        .flatMap(deliveryRepository::save);
  }

  @Override
  public Mono<Delivery> setComplete(Delivery newDelivery) {
    return deliveryRepository
        .findById(newDelivery.getId())
        .switchIfEmpty(Mono.error(DeliveryNotFoundException::new))
        .flatMap(
            existingDelivery -> DeliveryValidator.canSetComplete(existingDelivery, newDelivery))
        .flatMap(deliveryRepository::save);
  }

  @Override
  public Mono<Delivery> findById(String id) {
    return deliveryRepository
        .findById(id)
        .switchIfEmpty(Mono.error(DeliveryNotFoundException::new));
  }

  @Override
  public Flux<Delivery> findAll(PageRequest pageRequest) {
    return deliveryRepository.findAllByOrderIdContaining("", pageRequest);
  }

  @Override
  public Mono<Boolean> isPickedUp(String id) {
    return deliveryRepository
        .findById(id)
        .flatMap(del -> Mono.just(del.getDeliveryStatus().equals(PICKED_UP)))
        .onErrorReturn(NullPointerException.class, false);
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

    private static Mono<Delivery> canSetPickUp(Delivery before, Delivery after) {
      if (!canUpdateStatus(ACCEPTED, before, after)) {
        String message =
            String.format(
                "픽업완료로 전환이 불가한 상태입니다.     기존주문상태: %s       요청한주문상태: %s",
                before.getDeliveryStatus(), after.getDeliveryStatus());
        return Mono.error(new IllegalStateException(message));
      }
      return Mono.just(after);
    }

    private static Mono<Delivery> canSetComplete(Delivery before, Delivery after) {
      if (!canUpdateStatus(PICKED_UP, before, after)) {
        String message =
            String.format(
                "배달완료로 전환이 불가한 상태입니다.     기존주문상태: %s       요청한주문상태: %s",
                before.getDeliveryStatus(), after.getDeliveryStatus());
        return Mono.error(new IllegalStateException(message));
      }
      return Mono.just(after);
    }

    private static boolean canUpdateStatus(
        DeliveryStatus expectedBeforeStatus, Delivery before, Delivery after) {
      boolean statusSameAsExpected = before.getDeliveryStatus().equals(expectedBeforeStatus);
      boolean isNext = before.getDeliveryStatus().getNext().equals(after.getDeliveryStatus());
      log.info("statusSameAsExpected {} && isNext {}", statusSameAsExpected, isNext);
      return statusSameAsExpected && isNext;
    }

    private static Mono<Delivery> monoJustOrError(Delivery after, StringBuilder errorMessage) {
      return errorMessage.length() == 0
          ? Mono.just(after)
          : Mono.error(new IllegalArgumentException(errorMessage.toString()));
    }

    private static Mono<Delivery> statusIsAccepted(Delivery delivery) {
      return delivery.getDeliveryStatus().equals(DeliveryStatus.ACCEPTED)
          ? Mono.just(delivery)
          : Mono.error(new IllegalStateException("주문상태가 ACCEPTED가 아닙니다"));
    }

    private static Mono<Delivery> statusIsNotNull(Delivery delivery) {
      return Objects.isNull(delivery.getDeliveryStatus())
          ? Mono.error(new IllegalStateException("주문상태가 null 입니다."))
          : Mono.just(delivery);
    }

    private static Mono<Delivery> pickupTimeIsAfterOrderTime(Delivery delivery) {
      return delivery.getPickupTime().isAfter(delivery.getOrderTime())
          ? Mono.just(delivery)
          : Mono.error(new IllegalStateException("픽업시간은 주문시간 이후여야 합니다."));
    }
  }
}
