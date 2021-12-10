package com.inbobwetrust.controller;

import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping("status/{orderId}")
    public ResponseEntity<DeliveryStatus> getDeliveryStatus(@PathVariable String orderId) {
        DeliveryStatus deliveryStatus = deliveryService.findDeliveryStatusByOrderId(orderId);
        return new ResponseEntity<>(deliveryStatus, HttpStatus.OK);
    }
}
