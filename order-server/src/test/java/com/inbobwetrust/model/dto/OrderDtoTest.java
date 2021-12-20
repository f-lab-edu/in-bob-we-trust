package com.inbobwetrust.model.dto;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public class OrderDtoTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeClass
    @Test
    @DisplayName("[OrderDto] Validation 테스트")
    void newTest() {
        OrderDto orderDto =
                OrderDto.builder()
                        .id(1L)
                        .customerId(2L)
                        .shopId(3L)
                        .address("서울시 강남구 34-2 202호")
                        .phoneNumber("01032321232")
                        .phoneNumber("01032321232")
                        .createdAt(LocalDateTime.now())
                        .build();

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
