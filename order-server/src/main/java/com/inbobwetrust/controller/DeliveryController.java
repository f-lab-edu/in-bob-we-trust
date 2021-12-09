package com.inbobwetrust.controller;

import com.inbobwetrust.model.vo.Delivery;
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

    @PatchMapping("status/pickup")
    public ResponseEntity<Delivery> updateDeliveryStatusPickup(
            @RequestBody Delivery deliveryRequest) {
        Delivery delivery = deliveryService.updateDeliveryStatusPickup(deliveryRequest);
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }
}
