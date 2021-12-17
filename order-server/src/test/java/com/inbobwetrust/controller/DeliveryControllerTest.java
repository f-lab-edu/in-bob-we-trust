package com.inbobwetrust.controller;

import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse;
import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeSimpleNumberedDelivery;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.service.DeliveryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMybatis
public class DeliveryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DeliveryService deliveryService;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("라이더 픽업상태 변경 API")
    void setStatusToPickup_successTest() throws Exception {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        deliveryRequest.setStatus("pickedUp");
        when(this.deliveryService.setStatusPickup(deliveryRequest)).thenReturn(deliveryRequest);
        String requestBody = mapper.writeValueAsString(deliveryRequest);

        mockMvc.perform(
                        patch("/delivery/status/pickup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body.orderId", is(deliveryRequest.getOrderId())))
                .andExpect(jsonPath("$.body.riderId", is(deliveryRequest.getRiderId())))
                .andExpect(jsonPath("$.body.status", is(deliveryRequest.getStatus())))
                .andReturn();
    }

    @Test
    @DisplayName("배달대행사 라이더배정 테스트")
    void setRider_successTest() throws Exception {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        when(this.deliveryService.setRider(deliveryRequest)).thenReturn(deliveryRequest);

        String requestBody = mapper.writeValueAsString(deliveryRequest);

        mockMvc.perform(
                        put("/delivery/rider")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body.orderId", is(deliveryRequest.getOrderId())))
                .andExpect(jsonPath("$.body.riderId", is(deliveryRequest.getRiderId())))
                .andExpect(
                        jsonPath(
                                "$.body.deliveryAgentId", is(deliveryRequest.getDeliveryAgentId())))
                .andReturn();
    }

    @DisplayName("사장님 주문접수완료 성공")
    void addDelivery_successTest() throws Exception {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        Delivery expectedResponse = makeDeliveryForRequestAndResponse().get(1);
        when(this.deliveryService.addDelivery(deliveryRequest)).thenReturn(expectedResponse);

        String requestBody = mapper.writeValueAsString(deliveryRequest);

        MvcResult result =
                mockMvc.perform(
                                post("/delivery")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andReturn();

        Delivery responseObj =
                mapper.readValue(result.getResponse().getContentAsString(), Delivery.class);
        assertEquals(expectedResponse.getOrderId(), responseObj.getOrderId());
        assertEquals(expectedResponse.getRiderId(), responseObj.getRiderId());
        assertEquals(expectedResponse.getWantedPickupTime(), responseObj.getWantedPickupTime());
        assertEquals(
                expectedResponse.getEstimatedDeliveryFinishTime(),
                responseObj.getEstimatedDeliveryFinishTime());
    }

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
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.body.orderId", is(deliveryRequest.getOrderId())))
                        .andExpect(jsonPath("$.body.riderId", is(deliveryRequest.getRiderId())))
                        .andExpect(jsonPath("$.body.status", is(deliveryRequest.getStatus())))
                        .andExpect(
                                jsonPath(
                                        "$.body.deliveryAgentId",
                                        is(deliveryRequest.getDeliveryAgentId())))
                        .andReturn();
    }

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
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body.status", is(deliveryStatus.getStatus())));
    }
}
