package com.inbobwetrust.controller;

import com.inbobwetrust.common.ApiResult;
import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import static com.inbobwetrust.common.ApiUtil.successResponse;

@RestController
@RequestMapping("delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping("status/{orderId}")
    public ResponseEntity<ApiResult<DeliveryStatus>> getDeliveryStatus(
            @PathVariable String orderId) {
        DeliveryStatus deliveryStatus = deliveryService.findDeliveryStatusByOrderId(orderId);
        return successResponse(deliveryStatus);
    }

    @PatchMapping("status/pickup")
    public ResponseEntity<ApiResult<Delivery>> setStatusToPickup(
            @RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.setStatusPickup(deliveryRequest);
        return successResponse(delivery);
    }

    @PutMapping("rider")
    public ResponseEntity<ApiResult<Delivery>> setRider(@RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.setRider(deliveryRequest);
        return successResponse(delivery);
    }

    @PostMapping
    public ResponseEntity<ApiResult<Delivery>> addDelivery(@RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.addDelivery(deliveryRequest);
        return successResponse(delivery);
    }

    @PatchMapping("status/complete")
    public ResponseEntity<ApiResult<Delivery>> setStatusComplete(
            @RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.setStatusComplete(deliveryRequest);
        return successResponse(delivery);
    }
}
