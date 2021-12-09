package com.inbobwetrust.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.service.DeliveryService;

import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeSimpleNumberedDelivery;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryController.class)
public class DeliveryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DeliveryService deliveryService;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("라이더 배달완료 API")
    void setStatusToComplete_successTest() throws Exception {
        Delivery deliveryRequest = makeSimpleNumberedDelivery(1);
        deliveryRequest.setStatus("complete");
        when(this.deliveryService.setStatusComplete(deliveryRequest)).thenReturn(deliveryRequest);
        String requestBody = mapper.writeValueAsString(deliveryRequest);

        MvcResult result =
                mockMvc.perform(
                                patch("/delivery/status/complete")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.orderId", is(deliveryRequest.getOrderId())))
                        .andExpect(jsonPath("$.riderId", is(deliveryRequest.getRiderId())))
                        .andExpect(jsonPath("$.status", is(deliveryRequest.getStatus())))
                        .andExpect(
                                jsonPath(
                                        "$.deliveryAgentId",
                                        is(deliveryRequest.getDeliveryAgentId())))
                        .andReturn();
    }
}
