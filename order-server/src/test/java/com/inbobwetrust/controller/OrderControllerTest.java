package com.inbobwetrust.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.model.dto.OrderDto;
import com.inbobwetrust.model.entity.Order;
import com.inbobwetrust.model.mapper.OrderMapper;
import com.inbobwetrust.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@WebMvcTest(OrderController.class)
@AutoConfigureMybatis
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private OrderService orderService;

    @MockBean private OrderMapper orderMapper;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Order order = Order.builder().id(1L).shopId(1L).build();
    OrderDto orderDto;

    @BeforeEach
    void setUp() {
        orderDto =
                OrderDto.builder()
                        .id(1L)
                        .customerId(2L)
                        .shopId(3L)
                        .address("서울시 강남구 34-2 202호")
                        .phoneNumber("01032321232")
                        .phoneNumber("01032321232")
                        .createdAt(LocalDateTime.now())
                        .build();
        when(orderMapper.toEntity(orderDto)).thenReturn(order);
    }

    @Test
    @DisplayName("[OrderController] 신규주문수신")
    public void receiveNewOrder_successTest() throws Exception {
        when(this.orderService.receiveNewOrder(any())).thenReturn(order);

        String requestBody = mapper.writeValueAsString(orderDto);

        mockMvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[OrderController] 신규주문수신 : Serialization 오류 발생")
    public void receiveNewOrder_failTest() throws Exception {
        when(this.orderService.receiveNewOrder(any())).thenReturn(order);

        String requestBody = "f30ae9ihrtoi3wiht9aw3ihtlkaeshaoryu3a0r5;iy2056fdhaklfhklad";

        mockMvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[OrderController] 신규주문수신 : 빈값 전송")
    public void receiveNewOrder_failTest3() throws Exception {
        when(this.orderService.receiveNewOrder(any())).thenReturn(order);

        String requestBody = mapper.writeValueAsString(new OrderDto());

        mockMvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[OrderController] 신규주문수신 : null 값 포함")
    public void receiveNewOrder_failTest5() throws Exception {
        when(this.orderService.receiveNewOrder(any())).thenReturn(order);

        OrderDto orderDtoWithNull =
                OrderDto.builder()
                        .id(1L)
                        .customerId(2L)
                        .shopId(3L)
                        //            .address("서울시 강남구 34-2 202호")
                        .phoneNumber("01032321232")
                        .phoneNumber("01032321232")
                        .createdAt(LocalDateTime.now())
                        .build();
        String requestBody = mapper.writeValueAsString(orderDtoWithNull);
        mockMvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[OrderController] 신규주문수신 : Content Type 미설정")
    public void receiveNewOrder_failTest0() throws Exception {
        when(this.orderService.receiveNewOrder(any())).thenReturn(order);

        String requestBody = mapper.writeValueAsString(orderDto);
        mockMvc.perform(post("/order").content(requestBody))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[OrderController] 신규주문수신 : Content Type 다르게 ")
    public void receiveNewOrder_failTest11() throws Exception {
        when(this.orderService.receiveNewOrder(any())).thenReturn(order);

        String requestBody = mapper.writeValueAsString(orderDto);
        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_NDJSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[OrderController] 신규주문수신 : Content 미포함 ")
    public void receiveNewOrder_failTest6() throws Exception {
        when(this.orderService.receiveNewOrder(any())).thenReturn(order);

        mockMvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }
}
