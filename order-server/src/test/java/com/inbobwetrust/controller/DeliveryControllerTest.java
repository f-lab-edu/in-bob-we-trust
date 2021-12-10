package com.inbobwetrust.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.service.DeliveryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean DeliveryService deliveryService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("주문상태확인 GET Request")
    void getDeliveryStatus() throws Exception {
        DeliveryStatus deliveryStatus = new DeliveryStatus("order-1", "cooking");
        when(deliveryService.findDeliveryStatusByOrderId(deliveryStatus.getOrderId()))
                .thenReturn(deliveryStatus);

        mockMvc.perform(
                        get("/delivery/status/" + deliveryStatus.getOrderId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(deliveryStatus.getStatus())));
    }
}
