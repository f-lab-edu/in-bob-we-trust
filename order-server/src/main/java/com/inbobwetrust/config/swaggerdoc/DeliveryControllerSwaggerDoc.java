package com.inbobwetrust.config.swaggerdoc;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.inbobwetrust.common.ApiResult;
import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.DeliveryStatus;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface DeliveryControllerSwaggerDoc {
    @ApiOperation(value = "주문상태확인", notes = "주문번호에 해당하는 주문상태 반환")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = ApiResult.class)})
    ResponseEntity<DeliveryStatus> getDeliveryStatus(String orderId);

    @ApiOperation(value = "픽업완료", notes = "주문의 상태를 픽업완료로 갱신")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = ApiResult.class)})
    ResponseEntity<Delivery> setStatusToPickup(@RequestBody Delivery deliveryRequest);

    @ApiOperation(value = "라이더배정", notes = "배달정보에 라이더 정보를 설정")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = ApiResult.class)})
    ResponseEntity<Delivery> setRider(@RequestBody Delivery deliveryRequest);

    @ApiOperation(value = "주문접수완료", notes = "사장님이 주문접수완료")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = ApiResult.class)})
    ResponseEntity<Delivery> addDelivery(@RequestBody Delivery deliveryRequest);

    @ApiOperation(value = "배달완료", notes = "주문의 상태를 배달완료로 갱신")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = ApiResult.class)})
    ResponseEntity<Delivery> setStatusComplete(@RequestBody Delivery deliveryRequest);
}
