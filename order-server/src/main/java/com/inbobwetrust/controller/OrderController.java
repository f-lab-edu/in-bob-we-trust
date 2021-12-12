package com.inbobwetrust.controller;

import com.inbobwetrust.common.ApiResult;
import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.inbobwetrust.common.ApiUtil.successResponse;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResult<Order>> receiveNewOrder(@RequestBody Order newOrder) {
        Order order = orderService.receiveNewOrder(newOrder);
        return successResponse(order);
    }
}
