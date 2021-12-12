package com.inbobwetrust.controller;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.service.DeliveryService;
import io.swagger.annotations.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RestController
@RequestMapping("delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @ApiOperation(
            value = "주문상태확인",
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
    @GetMapping("status/{orderId}")
    public ResponseEntity<DeliveryStatus> getDeliveryStatus(@PathVariable String orderId) {
        DeliveryStatus deliveryStatus = deliveryService.findDeliveryStatusByOrderId(orderId);
        return new ResponseEntity<>(deliveryStatus, HttpStatus.OK);
    }

    @PatchMapping("status/pickup")
    public ResponseEntity<Delivery> setStatusToPickup(@RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.setStatusPickup(deliveryRequest);
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    @PutMapping("rider")
    public ResponseEntity<Delivery> setRider(@RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.setRider(deliveryRequest);
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Delivery> addDelivery(@RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.addDelivery(deliveryRequest);
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    @PatchMapping("status/complete")
    public ResponseEntity<Delivery> setStatusComplete(@RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.setStatusComplete(deliveryRequest);
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }
}
