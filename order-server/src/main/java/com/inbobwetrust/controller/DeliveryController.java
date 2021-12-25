package com.inbobwetrust.controller;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.service.DeliveryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    Mono<Delivery> addDelivery(@RequestBody @Valid Delivery delivery) {
        return deliveryService.addDelivery(delivery);
    }
}
