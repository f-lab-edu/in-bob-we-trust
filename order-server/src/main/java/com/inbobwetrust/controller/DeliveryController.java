package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.DeliveryControllerSwaggerDoc;
import com.inbobwetrust.model.dto.DeliveryCreateDto;
import com.inbobwetrust.model.dto.DeliverySetRiderDto;
import com.inbobwetrust.model.dto.DeliveryStatusDto;
import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.mapper.DeliveryMapper;
import com.inbobwetrust.service.DeliveryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryControllerSwaggerDoc {
    private final DeliveryService deliveryService;
    private final DeliveryMapper deliveryMapper;

    @GetMapping("status/{orderId}")
    public DeliveryStatusDto getDeliveryStatus(@PathVariable Long orderId) {
        return deliveryService.findDeliveryStatusByOrderId(orderId);
    }

    @PatchMapping("status/pickup")
    public Delivery setStatusToPickup(@RequestBody Delivery deliveryRequest) {
        return deliveryService.setStatusPickup(deliveryRequest);
    }

    @PutMapping("rider")
    public DeliverySetRiderDto setRider(
            @RequestBody @Valid DeliverySetRiderDto deliverySetRiderDto) {
        deliveryService.setRider(deliveryMapper.fromSetRiderDtoToEntity(deliverySetRiderDto));
        return deliverySetRiderDto;
    }

    @PostMapping
    public DeliveryCreateDto addDelivery(@RequestBody @Valid DeliveryCreateDto deliveryCreateDto) {
        deliveryService.addDelivery(deliveryMapper.fromCreateDtoToEntity(deliveryCreateDto));
        return deliveryCreateDto;
    }

    @PatchMapping("status/complete")
    public Delivery setStatusComplete(@RequestBody Delivery deliveryStatusDto) {
        return deliveryService.setStatusComplete(deliveryStatusDto);
    }
}
