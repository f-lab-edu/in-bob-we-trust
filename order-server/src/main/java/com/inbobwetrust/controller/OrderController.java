package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.OrderControllerSwaggerDoc;
import com.inbobwetrust.model.dto.OrderDto;
import com.inbobwetrust.model.entity.Order;
import com.inbobwetrust.model.mapper.OrderMapper;
import com.inbobwetrust.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
@Validated
class OrderController implements OrderControllerSwaggerDoc {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public OrderDto receiveNewOrder(@RequestBody OrderDto orderDto) {
        orderService.receiveNewOrder(orderMapper.toEntity(orderDto));
        return orderDto;
    }
}
