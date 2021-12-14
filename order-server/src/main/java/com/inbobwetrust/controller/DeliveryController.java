package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.DeliveryControllerSwaggerDoc;
import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.service.DeliveryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryControllerSwaggerDoc {
    private final DeliveryService deliveryService;

    @GetMapping("status/{orderId}")
    public DeliveryStatus getDeliveryStatus(@PathVariable String orderId) {
        return deliveryService.findDeliveryStatusByOrderId(orderId);
    }

    @PatchMapping("status/pickup")
    public Delivery setStatusToPickup(@RequestBody Delivery deliveryRequest) {
        return deliveryService.setStatusPickup(deliveryRequest);
    }

    @PutMapping("rider")
    public Delivery setRider(@RequestBody Delivery deliveryRequest) {
        return deliveryService.setRider(deliveryRequest);
    }

    @PostMapping
    public Delivery addDelivery(@RequestBody Delivery deliveryRequest) {
        return deliveryService.addDelivery(deliveryRequest);
    }

    @PatchMapping("status/complete")
    public Delivery setStatusComplete(@RequestBody Delivery deliveryRequest) {
        return deliveryService.setStatusComplete(deliveryRequest);
    }
}
