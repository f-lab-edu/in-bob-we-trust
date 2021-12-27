package com.inbobwetrust.config.swaggerdoc;

import com.inbobwetrust.domain.Delivery;
import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

public interface DeliveryControllerSwaggerDoc {
  @Operation(summary = "신규배달 중계추가")
  public Mono<Delivery> addDelivery(Delivery delivery);

  @Operation(summary = "사장님 주문접수")
  public Mono<Delivery> acceptDelivery(Delivery delivery);

  @Operation(summary = "라이더배정")
  public Mono<Delivery> setDeliveryRider(Delivery delivery);

  @Operation(summary = "주문픽업")
  public Mono<Delivery> setPickedUp(Delivery delivery);

  @Operation(summary = "배달완료")
  public Mono<Delivery> setComplete(Delivery delivery);
}
