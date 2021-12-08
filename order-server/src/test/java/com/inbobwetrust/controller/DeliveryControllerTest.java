package com.inbobwetrust.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.service.DeliveryService;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        LocalDateTime wantedPickupTime = LocalDateTime.now().plusMinutes(30);
        Delivery deliveryRequest =
                Delivery.builder()
                        .orderId("order-1")
                        .riderId("rider-1")
                        .wantedPickupTime(wantedPickupTime)
                        .build();
        Delivery expectedDeliveryResponse =
                Delivery.builder()
                        .orderId(deliveryRequest.getOrderId())
                        .riderId(deliveryRequest.getRiderId())
                        .wantedPickupTime(deliveryRequest.getWantedPickupTime())
                        .estimatedDeliveryFinishTime(wantedPickupTime.plusMinutes(30))
                        .build();
        when(this.deliveryService.addDelivery(deliveryRequest))
                .thenReturn(expectedDeliveryResponse);
        String requestBody = mapper.writeValueAsString(deliveryRequest);

        MvcResult result =
                mockMvc.perform(
                                post("/delivery")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        Delivery responseObj =
                mapper.readValue(result.getResponse().getContentAsString(), Delivery.class);
        assertEquals(expectedDeliveryResponse.getOrderId(), responseObj.getOrderId());
        assertEquals(expectedDeliveryResponse.getRiderId(), responseObj.getRiderId());
        assertEquals(
                expectedDeliveryResponse.getWantedPickupTime(), responseObj.getWantedPickupTime());
        assertEquals(
                expectedDeliveryResponse.getEstimatedDeliveryFinishTime(),
                responseObj.getEstimatedDeliveryFinishTime());
    }

    @Test
    void LocalDateTimeTest() {
        System.out.println(LocalDateTime.now());
        System.out.println(LocalDateTime.now().plusMinutes(30L));
    }
}
