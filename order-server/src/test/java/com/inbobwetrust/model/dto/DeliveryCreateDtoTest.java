package com.inbobwetrust.model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public class DeliveryCreateDtoTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("[DeliveryCreateDto] Validation 정상")
    void newTest_success1() {
        DeliveryCreateDto deliveryCreateDto =
                DeliveryCreateDto.builder()
                        .orderId(1L)
                        .agencyId(1L)
                        .pickupTime(LocalDateTime.now().plusMinutes(30))
                        .createdAt(LocalDateTime.now())
                        .build();

        Set<ConstraintViolation<DeliveryCreateDto>> violations =
                validator.validate(deliveryCreateDto);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("[DeliveryCreateDto] Validation 실패 : Long 범위 밖")
    void newTest_success2() {
        DeliveryCreateDto deliveryCreateDto =
                DeliveryCreateDto.builder()
                        .orderId(0L)
                        .agencyId(0L)
                        .pickupTime(LocalDateTime.now().plusMinutes(30))
                        .createdAt(LocalDateTime.now())
                        .build();

        Set<ConstraintViolation<DeliveryCreateDto>> violations =
                validator.validate(deliveryCreateDto);
        Assertions.assertEquals(2, violations.size());
    }

    @Test
    @DisplayName("[DeliveryCreateDto] Validation 실패 : 전부 null")
    void newTest_success3() {
        DeliveryCreateDto deliveryCreateDto = DeliveryCreateDto.builder().build();

        Set<ConstraintViolation<DeliveryCreateDto>> violations =
                validator.validate(deliveryCreateDto);
        Assertions.assertEquals(4, violations.size());
    }
}
