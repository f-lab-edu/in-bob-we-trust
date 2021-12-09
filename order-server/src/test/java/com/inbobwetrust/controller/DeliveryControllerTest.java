package com.inbobwetrust.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.service.DeliveryService;

import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("사장님 주문접수요청 성공")
    void addDelivery_successTest() throws Exception {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        when(this.deliveryService.addDelivery(deliveryRequest)).thenReturn(deliveryRequest);
        String requestBody = mapper.writeValueAsString(deliveryRequest);

        MvcResult result =
                mockMvc.perform(
                                post("/delivery")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.orderId", is(deliveryRequest.getOrderId())))
                        .andExpect(jsonPath("$.riderId", is(deliveryRequest.getRiderId())))
                        .andExpect(
                                jsonPath(
                                        "$.deliveryAgentId",
                                        is(deliveryRequest.getDeliveryAgentId())))
                        .andReturn();
    }

    @Test
    @DisplayName("배달대행사 라이더배정 테스트")
    void setRider_successTest() throws Exception {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        when(this.deliveryService.setRider(deliveryRequest)).thenReturn(deliveryRequest);
        String requestBody = mapper.writeValueAsString(deliveryRequest);

        MvcResult result =
                mockMvc.perform(
                                put("/delivery/rider")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.orderId", is(deliveryRequest.getOrderId())))
                        .andExpect(jsonPath("$.riderId", is(deliveryRequest.getRiderId())))
                        .andExpect(
                                jsonPath(
                                        "$.deliveryAgentId",
                                        is(deliveryRequest.getDeliveryAgentId())))
                        .andReturn();
    }

    @Test
    @DisplayName("라이더 픽업상태 변경 API")
    void setStatusToPickup_successTest() throws Exception {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        deliveryRequest.setStatus("pickedUp");
        when(this.deliveryService.updateDeliveryStatusPickup(deliveryRequest))
                .thenReturn(deliveryRequest);
        String requestBody = mapper.writeValueAsString(deliveryRequest);

        MvcResult result =
                mockMvc.perform(
                                patch("/delivery/status/pickup")
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
