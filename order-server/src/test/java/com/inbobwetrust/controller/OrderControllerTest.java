package com.inbobwetrust.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private OrderService orderService;

    private ObjectMapper mapper = new ObjectMapper();
    Order orderToSave = Order.builder().id("order-1").shopId("shop-1").build();

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("신규주문수신 테스트")
    public void receiveNewOrder_successTest() throws Exception {
        when(this.orderService.receiveNewOrder(orderToSave))
                .thenReturn(Order.builder().id("order-1").shopId("shop-1").build());
        String requestBody = mapper.writeValueAsString(orderToSave);

        mockMvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.body.id", is(orderToSave.getId())))
                .andExpect(jsonPath("$.body.shopId", is(orderToSave.getShopId())));
    }
}
