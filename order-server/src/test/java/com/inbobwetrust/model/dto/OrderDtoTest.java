package com.inbobwetrust.model.dto;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

public class OrderDtoTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeClass
    @Test
    @DisplayName("[OrderDto] Validation 테스트")
    void newTest() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setShopId(1L);
        orderDto.setCustomerId(2L);
        orderDto.setAddress("someAddress");
        orderDto.setPhoneNumber("01031232132");
        orderDto.setCreatedAt(LocalDateTime.now());

        Set<ConstraintViolation<OrderDto>> violations = validator.validate(orderDto);
        Assertions.assertEquals(0, violations.size());
    }

    @BeforeClass
    @Test
    @DisplayName("[OrderDto] Validation 테스트")
    void newTest_bad() {
        OrderDto orderDto = new OrderDto();

        Set<ConstraintViolation<OrderDto>> violations = validator.validate(orderDto);
        Assertions.assertEquals(6, violations.size());
    }
}
