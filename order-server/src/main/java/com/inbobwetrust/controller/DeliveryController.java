package com.inbobwetrust.controller;

import com.inbobwetrust.model.vo.Delivery;
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
