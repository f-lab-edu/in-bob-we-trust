package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.OrderControllerSwaggerDoc;
import com.inbobwetrust.model.dto.OrderDto;
import com.inbobwetrust.model.mapper.OrderMapper;
import com.inbobwetrust.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
class OrderController implements OrderControllerSwaggerDoc {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public OrderDto receiveNewOrder(@RequestBody @Valid OrderDto orderDto) {
        orderService.receiveNewOrder(orderMapper.toEntity(orderDto));
        return orderDto;
    }
}
