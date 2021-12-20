package com.inbobwetrust.model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class DeliverySetRiderDtoTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("[DeliverySetRiderDto] Validation 정상")
    void newTest_success1() {
        DeliverySetRiderDto deliverySetRiderDto =
                DeliverySetRiderDto.builder().orderId(1L).riderId(2L).agencyId(3L).build();

        Set<ConstraintViolation<DeliverySetRiderDto>> violations =
                validator.validate(deliverySetRiderDto);

        Assertions.assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("[DeliverySetRiderDto] Validation 실패 전부 null")
    void newTest_success2() {
        DeliverySetRiderDto deliverySetRiderDto = DeliverySetRiderDto.builder().build();

        Set<ConstraintViolation<DeliverySetRiderDto>> violations =
                validator.validate(deliverySetRiderDto);

        Assertions.assertEquals(3, violations.size());
    }

    @Test
    @DisplayName("[DeliverySetRiderDto] Validation 실패 : Long 범위 밖")
    void newTest_success3() {
        DeliverySetRiderDto deliverySetRiderDto =
                DeliverySetRiderDto.builder().orderId(0L).riderId(0L).agencyId(0L).build();

        Set<ConstraintViolation<DeliverySetRiderDto>> violations =
                validator.validate(deliverySetRiderDto);

        Assertions.assertEquals(3, violations.size());
    }
}
