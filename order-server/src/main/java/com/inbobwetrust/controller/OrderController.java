package com.inbobwetrust.controller;

import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> receiveNewOrder(@RequestBody Order newOrder) {
        Order order = orderService.receiveNewOrder(newOrder);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
