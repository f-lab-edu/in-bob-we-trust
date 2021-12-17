package com.inbobwetrust.config.swaggerdoc;

import com.inbobwetrust.aop.ApiResult;
import com.inbobwetrust.model.entity.Order;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

public interface OrderControllerSwaggerDoc {
    @ApiOperation(value = "신규주문접수", notes = "배달의민족 주문서버로부터 주문정보를 전송받는 API")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = ApiResult.class)})
    Order receiveNewOrder(Order newOrder);
}
