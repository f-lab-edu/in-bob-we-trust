package com.inbobwetrust.controller;

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
import com.inbobwetrust.model.dto.DeliveryGetStatusDto;
import com.inbobwetrust.model.dto.DeliverySetRiderDto;
import com.inbobwetrust.model.dto.DeliveryStatusDto;
import com.inbobwetrust.model.entity.Delivery;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
    DeliverySetRiderDto deliverySetRiderDto = makeSimpleDeliverySetRiderDto();
    DeliveryStatusDto deliveryStatusDto = makeSimpleDeliveryStatusDto();

    Delivery delivery = makeSimpleNumberedDelivery(1);

    @BeforeEach
    void setUp() {
        when(deliveryMapper.fromCreateDtoToEntity(deliveryCreateDto)).thenReturn(delivery);
        when(deliveryMapper.fromSetRiderDtoToEntity(deliverySetRiderDto)).thenReturn(delivery);
        when(this.deliveryService.addDelivery(any())).thenReturn(delivery);
        when(this.deliveryService.setRider(any())).thenReturn(delivery);
        when(this.deliveryService.setStatusPickup(any())).thenReturn(delivery);
        when(this.deliveryService.setStatusComplete(any())).thenReturn(delivery);
        when(deliveryService.findDeliveryStatusByOrderId(deliveryStatusDto.getOrderId()))
                .thenReturn(deliveryStatusDto);
    }

    @Test
    @DisplayName("라이더 픽업상태 변경 API")
    void setStatusToPickup_successTest() throws Exception {
        String requestBody = mapper.writeValueAsString(makeSimpleDeliveryStatusDto());

        testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                patch("/delivery/status/pickup"), requestBody, status().isOk(), successful());
    }

    private DeliverySetRiderDto makeSimpleDeliverySetRiderDto() {
        return DeliverySetRiderDto.builder().orderId(1L).riderId(2L).agencyId(3L).build();
    }

    @Test
    @DisplayName("[DeliveryController.setRider] 성공 : 라이더배정 성공")
    void setRider_successTest() throws Exception {
        DeliverySetRiderDto expected = makeSimpleDeliverySetRiderDto();
        String requestBody = mapper.writeValueAsString(expected);

        String responseBody =
                testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                                put("/delivery/rider"), requestBody, status().isOk(), successful())
                        .getResponse()
                        .getContentAsString();

        String bodyString =
                mapper.writeValueAsString(mapper.readValue(responseBody, Map.class).get("body"));
        DeliverySetRiderDto actual = mapper.readValue(bodyString, DeliverySetRiderDto.class);
        assertEquals(expected, actual);
    }

    private DeliveryCreateDto makeSimpleDeliveryCreateDto() {
        return DeliveryCreateDto.builder()
                .orderId(1L)
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
        assertTrue(expectedMap.containsKey("orderId"));
        BigInteger greaterThanLongMaxValue =
                new BigInteger(String.valueOf(Long.MAX_VALUE).concat("1000"));
        expectedMap.put("orderId", greaterThanLongMaxValue);

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
                mapper.writeValueAsString(new DeliveryCreateDto(null, null, null, null));

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
    @DisplayName("[DeliveryController.setStatusToComplete] 성공 : 배달상태 업데이트, 배달완료")
    void setStatusToComplete_successTest() throws Exception {
        DeliveryStatusDto deliveryStatusDto = makeSimpleDeliveryStatusDto();
        String requestBody = mapper.writeValueAsString(deliveryStatusDto);

        testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                patch("/delivery/status/complete"), requestBody, status().isOk(), successful());
    }

    @Test
    @DisplayName("[DeliveryController.getDeliveryStatus] 성공 : 배달상태확인")
    void getDeliveryStatus() throws Exception {
        DeliveryGetStatusDto deliveryGetStatusDto =
                DeliveryGetStatusDto.builder().orderId(1L).build();
        String requestBody = mapper.writeValueAsString(deliveryGetStatusDto);

        testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                post("/delivery/status/" + deliveryStatusDto.getOrderId()),
                requestBody,
                status().isOk(),
                successful());
    }

    @Test
    @DisplayName("[DeliveryController.setRider] 실패 : 전부 MIN(1) 벗어난 값들 전송")
    void setRider_failTest2() throws Exception {
        DeliverySetRiderDto invalid =
                DeliverySetRiderDto.builder().riderId(0L).orderId(0L).agencyId(0L).build();
        String bodyInvalid = mapper.writeValueAsString(invalid);

        testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                put("/delivery/rider"), bodyInvalid, status().isNotAcceptable(), !successful());
    }

    @Test
    @DisplayName("[DeliveryController.setRider] 실패 : invalid 값")
    void setRider_failTest() throws Exception {
        DeliverySetRiderDto empty = DeliverySetRiderDto.builder().build();
        String bodyEmpty = mapper.writeValueAsString(empty);

        testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                put("/delivery/rider"), bodyEmpty, status().isNotAcceptable(), !successful());
    }

    MvcResult testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
            MockHttpServletRequestBuilder requestBuilder,
            String requestBody,
            ResultMatcher resultStatus,
            boolean successful)
            throws Exception {
        return mockMvc.perform(
                        requestBuilder.contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(resultStatus)
                .andExpect(jsonPath("$.success", is(successful)))
                .andExpect(jsonPath("$.body").exists())
                .andReturn();
    }

    private boolean successful() {
        return true;
    }

    private DeliveryStatusDto makeSimpleDeliveryStatusDto() {
        return DeliveryStatusDto.builder()
                .orderId(1L)
                .deliveryId(1L)
                .riderId(1L)
                .orderStatus(OrderStatus.PICKED_UP)
                .build();
    }

    @Test
    @DisplayName("[DeliveryController.setStatusToPickup] 실패 : 정보누락")
    void setStatusToPickupTest_fail() throws Exception {
        String requestBody = mapper.writeValueAsString(new DeliveryStatusDto());

        testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                patch("/delivery/status/pickup"),
                requestBody,
                status().isNotAcceptable(),
                !successful());
    }

    @Test
    @DisplayName("[DeliveryController.setStatusToComplete] 실패 : 배달완료 정보누락")
    void setStatusToCompleteTest_fail() throws Exception {
        String requestBody = mapper.writeValueAsString(new DeliveryStatusDto());

        testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                patch("/delivery/status/complete"),
                requestBody,
                status().isNotAcceptable(),
                !successful());
    }

    @Test
    @DisplayName("[DeliveryController.getDeliveryStatus] 실패 : 배달상태확인 정보누락")
    void getDeliveryStatusTest_fail() throws Exception {
        DeliveryGetStatusDto deliveryGetStatusDto = DeliveryGetStatusDto.builder().build();
        String requestBody = mapper.writeValueAsString(deliveryGetStatusDto);

        MvcResult result =
                testRequest_withBody_expectedStatus_checkSuccessful_returnMvcResult(
                        post("/delivery/status/" + deliveryStatusDto.getOrderId()),
                        requestBody,
                        status().isNotAcceptable(),
                        !successful());
    }
}
