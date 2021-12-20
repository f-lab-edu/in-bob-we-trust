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
        OrderDto expected = new OrderDto();
        expected.setId(1L);
        expected.setShopId(1L);
        expected.setCustomerId(2L);
        expected.setAddress("someAddress");
        expected.setPhoneNumber("01031232132");
        expected.setCreatedAt(LocalDateTime.now());

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
