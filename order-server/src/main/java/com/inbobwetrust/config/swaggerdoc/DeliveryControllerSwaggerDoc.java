package com.inbobwetrust.config.swaggerdoc;

import com.inbobwetrust.domain.Delivery;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface DeliveryControllerSwaggerDoc {
  @ApiOperation(value = "신규배달 중계추가", notes = "저장된 배달정보 반환")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = Delivery.class)})
  public Mono<Delivery> addDelivery(Delivery delivery);

  @ApiOperation(value = "사장님 주문접수", notes = "저장된 배달정보 반환")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = Delivery.class)})
  public Mono<Delivery> acceptDelivery(Delivery delivery);

  @ApiOperation(value = "라이더배정", notes = "저장된 배달정보 반환")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = Delivery.class)})
  public Mono<Delivery> setDeliveryRider(Delivery delivery);

  @ApiOperation(value = "주문픽업", notes = "저장된 배달정보 반환")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = Delivery.class)})
  public Mono<Delivery> setPickedUp(Delivery delivery);

  @ApiOperation(value = "배달완료", notes = "저장된 배달정보 반환")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = Delivery.class)})
  public Mono<Delivery> setComplete(Delivery delivery);
}
