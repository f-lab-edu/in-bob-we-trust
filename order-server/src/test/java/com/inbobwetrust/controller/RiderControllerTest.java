package com.inbobwetrust.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.model.dto.RiderLocationDto;
import com.inbobwetrust.model.entity.RiderLocation;
import com.inbobwetrust.model.mapper.RiderMapper;
import com.inbobwetrust.service.RiderLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiderController.class)
@AutoConfigureMybatis
public class RiderControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RiderLocationService riderLocationService;

    @MockBean private RiderMapper riderMapper;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    RiderLocation riderLocation =
            RiderLocation.builder()
                    .riderId(1L)
                    .longitude(RiderLocation.MAX_LONGITUDE)
                    .latitude(RiderLocation.MAX_LATITUDE)
                    .build();

    RiderLocationDto riderLocationDto;

    @BeforeEach
    void setUp() {
        riderLocationDto =
                RiderLocationDto.builder()
                        .riderId(riderLocation.getRiderId())
                        .longitude(riderLocation.getLongitude())
                        .latitude(riderLocation.getLatitude())
                        .build();
        when(riderMapper.toLocationEntity(riderLocationDto)).thenReturn(riderLocation);
        when(this.riderLocationService.updateLocation(any())).thenReturn(riderLocation);
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 라이더 위치 업데이트 API")
    void setRiderLocation_successTest() throws Exception {
        String requestBody = objectMapper.writeValueAsString(riderLocation);

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 신규주문수신 : toJSON 오류, 전혀다른 값")
    public void setRiderLocation_failTest() throws Exception {
        String requestBody = "fjeioawhfowha34f9o34yaw98ty4awothaikhgkahgiowhgoa";

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 신규주문수신 : toJSON 오류, 미세하게 다른 값 앞부분")
    public void setRiderLocation_failTest2() throws Exception {
        String JSONstring = objectMapper.writeValueAsString(riderLocationDto);
        String requestBody = "ATTACK".concat(JSONstring);

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 신규주문수신 : 빈값 전송")
    public void setRiderLocation_failTest3() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new RiderLocationDto());

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 신규주문수신 : null 값 포함")
    public void setRiderLocation_failTest5() throws Exception {
        RiderLocationDto riderLocationDto =
                RiderLocationDto.builder()
                        .riderId(1L)
                        .longitude(RiderLocation.MAX_LONGITUDE + 1)
                        .latitude(RiderLocation.MAX_LATITUDE)
                        .build();
        String requestBody = objectMapper.writeValueAsString(riderLocationDto);

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 신규주문수신 : RiderLocationDTO 맥스 초과")
    public void setRiderLocation_failTest4() throws Exception {
        RiderLocationDto riderLocationDto =
                RiderLocationDto.builder()
                        .riderId(1L)
                        .longitude(RiderLocation.MAX_LONGITUDE + 1)
                        .latitude(RiderLocation.MAX_LATITUDE)
                        .build();
        String requestBody = objectMapper.writeValueAsString(riderLocationDto);

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 신규주문수신 : Content Type 다르게 ")
    public void setRiderLocation_failTest11() throws Exception {
        String requestBody = objectMapper.writeValueAsString(riderLocationDto);

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_NDJSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 신규주문수신 : Content 미포함 ")
    public void setRiderLocation_failTest6() throws Exception {
        mockMvc.perform(patch("/rider/location").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @DisplayName("[RiderController.setRiderLocation] 신규주문수신 : Content Type 미설정")
    public void setRiderLocation_failTest0() throws Exception {
        String requestBody = objectMapper.writeValueAsString(riderLocationDto);

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_NDJSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.body").exists());
    }
}
