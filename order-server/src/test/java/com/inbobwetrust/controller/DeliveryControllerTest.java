package com.inbobwetrust.controller;

import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse;
import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeSimpleNumberedDelivery;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.model.dto.DeliveryCreateDto;
import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.entity.DeliveryStatus;
import com.inbobwetrust.model.entity.OrderStatus;
import com.inbobwetrust.model.mapper.DeliveryMapper;
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

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMybatis
public class DeliveryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private DeliveryService deliveryService;

    @MockBean private DeliveryMapper deliveryMapper;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    DeliveryCreateDto deliveryCreateDto = makeSimpleDeliveryCreateDto();
    Delivery delivery = makeSimpleNumberedDelivery(1);

    @BeforeEach
    void setUp() {
        when(deliveryMapper.fromCreateDtoToEntity(deliveryCreateDto)).thenReturn(delivery);
        when(this.deliveryService.addDelivery(any())).thenReturn(delivery);
    }

    @Test
    @DisplayName("라이더 픽업상태 변경 API")
    void setStatusToPickup_successTest() throws Exception {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        deliveryRequest.setOrderStatus(OrderStatus.PICKED_UP);
        when(this.deliveryService.setStatusPickup(any())).thenReturn(deliveryRequest);
        String requestBody = mapper.writeValueAsString(deliveryRequest);

        mockMvc.perform(
                        patch("/delivery/status/pickup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body").exists())
                .andReturn();
    }

    @Test
    @DisplayName("배달대행사 라이더배정 테스트")
    void setRider_successTest() throws Exception {
        Delivery deliveryRequest = makeDeliveryForRequestAndResponse().get(0);
        when(this.deliveryService.setRider(any())).thenReturn(deliveryRequest);

        String requestBody = mapper.writeValueAsString(deliveryRequest);

        mockMvc.perform(
                        put("/delivery/rider")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body").exists())
                .andReturn();
    }

    private DeliveryCreateDto makeSimpleDeliveryCreateDto() {
        return DeliveryCreateDto.builder()
                .orderId(1L)
                .riderId(2L)
                .agencyId(3L)
                .pickupTime(LocalDateTime.now().plusMinutes(30))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("[DeliveryController.addDelivery] 성공 : 사장님 주문접수완료")
    void addDelivery_successTest() throws Exception {
        when(this.deliveryService.addDelivery(any())).thenReturn(any());
        DeliveryCreateDto expected = deliveryCreateDto;
        String requestBody = mapper.writeValueAsString(expected);

        String responseBody =
                mockMvc.perform(
                                post("/delivery")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.body").exists())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String bodyString =
                mapper.writeValueAsString(mapper.readValue(responseBody, Map.class).get("body"));
        DeliveryCreateDto actual = mapper.readValue(bodyString, DeliveryCreateDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("[DeliveryController.addDelivery] 성공 : Map.class로 변환 후 전송")
    void addDelivery_successTest2() throws Exception {
        when(this.deliveryService.addDelivery(any())).thenReturn(delivery);
        DeliveryCreateDto expected = deliveryCreateDto;

        String requestBody = mapper.writeValueAsString(mapper.convertValue(expected, Map.class));

        mockMvc.perform(
                        post("/delivery")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[DeliveryController.addDelivery] 실패: Long.MAX_VALUE 초과값 전송")
    void addDeliveryTest_fail() throws Exception {
        DeliveryCreateDto expected = deliveryCreateDto;
        Map expectedMap = mapper.convertValue(expected, Map.class); // Long.MAX_VALUE 초과값 전송
        assertTrue(expectedMap.containsKey("riderId"));
        BigInteger greaterThanLongMaxValue =
                new BigInteger(String.valueOf(Long.MAX_VALUE).concat("1000"));
        expectedMap.put("riderId", greaterThanLongMaxValue);

        String requestBody = mapper.writeValueAsString(expectedMap);

        mockMvc.perform(
                        post("/delivery")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[DeliveryController.addDelivery] 실패: 누락된(null) 정보")
    void addDeliveryTest_fail1() throws Exception {
        String requestBody =
                mapper.writeValueAsString(new DeliveryCreateDto(null, null, null, null, null));

        mockMvc.perform(
                        post("/delivery")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[DeliveryController.addDelivery] 실패:toJSON 오류, 전혀다른 값")
    void addDeliveryTest_fail3() throws Exception {
        String requestBody = "feikahflkeahfoizhlkfehawfo9iaehwofeihfoeawifhaeowihfoew37501";

        mockMvc.perform(
                        post("/delivery")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("라이더 배달완료 API")
    void setStatusToComplete_successTest() throws Exception {
        Delivery deliveryRequest = makeSimpleNumberedDelivery(1);
        deliveryRequest.setOrderStatus(OrderStatus.COMPLETE);

        when(this.deliveryService.setStatusComplete(any())).thenReturn(deliveryRequest);
        String requestBody = mapper.writeValueAsString(deliveryRequest);

        MvcResult result =
                mockMvc.perform(
                                patch("/delivery/status/complete")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.body").exists())
                        .andReturn();
    }

    @Test
    @DisplayName("주문상태확인 GET Request")
    void getDeliveryStatus() throws Exception {
        DeliveryStatus deliveryStatus = new DeliveryStatus(1L, OrderStatus.ACCEPTED);
        when(deliveryService.findDeliveryStatusByOrderId(deliveryStatus.getOrderId()))
                .thenReturn(deliveryStatus);

        mockMvc.perform(
                        get("/delivery/status/" + deliveryStatus.getOrderId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body").exists());
    }
}
