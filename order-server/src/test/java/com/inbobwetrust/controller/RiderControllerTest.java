package com.inbobwetrust.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inbobwetrust.model.entity.RiderLocation;
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

@WebMvcTest(RiderController.class)
@AutoConfigureMybatis
public class RiderControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RiderLocationService riderLocationService;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("라이더 위치 업데이트 API")
    void addDelivery_successTest() throws Exception {
        RiderLocation riderLocation =
                RiderLocation.builder().riderId(1L).latitude(90.0).latitude(180.0).build();
        when(this.riderLocationService.updateLocation(any())).thenReturn(riderLocation);
        String requestBody = mapper.writeValueAsString(riderLocation);

        mockMvc.perform(
                        patch("/rider/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.body").exists());
    }
}
