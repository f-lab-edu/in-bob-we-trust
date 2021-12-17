package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.OrderControllerSwaggerDoc;
import com.inbobwetrust.model.entity.Order;
import com.inbobwetrust.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
class OrderController implements OrderControllerSwaggerDoc {

    private final OrderService orderService;

    @PostMapping
    public Order receiveNewOrder(@RequestBody Order newOrder) {
        return orderService.receiveNewOrder(newOrder);
    }
}
