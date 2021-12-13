package com.inbobwetrust.config.swagger;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.inbobwetrust.model.vo.DeliveryStatus;

import io.swagger.annotations.*;

import org.springframework.http.ResponseEntity;

public interface DeliveryControllerSwaggerDoc {
    @ApiOperation(
            value = "주문상태확인 Hi.",
            notes = "Delivery 는 사장님이 주문을 접수할때 생성되며 배달완료 될 때까지 단계별 상태변환이 있습니다.")
    @ApiResponses(
            value = {
                @ApiResponse(code = 500, message = "Server error"),
                @ApiResponse(code = 404, message = "Service not found"),
                @ApiResponse(
                        code = 200,
                        message = "Successful retrieval",
                        response = DeliveryStatus.class)
            })
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = CONTENT_TYPE,
                value = APPLICATION_JSON,
                required = true,
                allowEmptyValue = false,
                paramType = "header",
                dataTypeClass = String.class,
                example = "application/json...?"),
    })
    ResponseEntity<DeliveryStatus> getDeliveryStatus(String orderId);
}
