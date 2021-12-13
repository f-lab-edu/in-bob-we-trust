package com.inbobwetrust.controller;

import com.inbobwetrust.model.vo.Order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("obj")
    public Order test2() {
        return Order.builder().id("order-1").shopId("shop-1").build();
    }

    @GetMapping("string")
    public String fail() {
        throw new IllegalArgumentException("barreh");
    }
}
