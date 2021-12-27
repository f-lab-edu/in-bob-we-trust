package com.inbobwetrust.config.swaggerdoc;

import com.inbobwetrust.domain.Delivery;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import java.util.Map;

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

  @Operation(summary = "모든 배달정보조회, queryParameter로 page와 size 입력필요.")
  public Flux<Delivery> getDeliveries(@RequestParam Map<String, Object> paging);

  @Operation(summary = "주문번호로 배달정보조회")
  public Mono<Delivery> getDelivery(@PathVariable @NotBlank(message = "배달번호가 비어있습니다.") String id);
}
