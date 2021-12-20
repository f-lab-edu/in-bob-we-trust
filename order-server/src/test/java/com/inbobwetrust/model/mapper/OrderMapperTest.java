package com.inbobwetrust.model.mapper;

import com.inbobwetrust.model.dto.OrderDto;
import com.inbobwetrust.model.entity.Order;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@ActiveProfiles("test")
@SpringBootTest
public class OrderMapperTest {

    @Autowired OrderMapper orderMapper;

    @Test
    @DisplayName("[OrderMapper.toEntity] ")
    void toEntity() {
        OrderDto expected =
                OrderDto.builder()
                        .id(1L)
                        .customerId(2L)
                        .shopId(3L)
                        .address("서울시 강남구 34-2 202호")
                        .phoneNumber("01032321232")
                        .phoneNumber("01032321232")
                        .createdAt(LocalDateTime.now())
                        .build();

        Order actual = orderMapper.toEntity(expected);

        Assertions.assertNull(actual.getUpdatedAt());
        Assertions.assertNull(actual.getOrderStatus());
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getShopId(), actual.getShopId());
        Assertions.assertEquals(expected.getCustomerId(), actual.getCustomerId());
        Assertions.assertEquals(expected.getAddress(), actual.getAddress());
        Assertions.assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
        Assertions.assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    }
}
