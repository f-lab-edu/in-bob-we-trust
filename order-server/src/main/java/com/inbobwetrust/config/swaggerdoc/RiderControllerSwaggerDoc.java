package com.inbobwetrust.config.swaggerdoc;

import com.inbobwetrust.common.ApiResult;
import com.inbobwetrust.model.vo.Rider;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.http.ResponseEntity;

public interface RiderControllerSwaggerDoc {
    @ApiOperation(value = "라이더위치", notes = "라이더의 위치정보를 갱신")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "성공", response = ApiResult.class)})
    ResponseEntity<Rider> setRiderLocation(Rider body);
}
