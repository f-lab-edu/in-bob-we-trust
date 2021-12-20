package com.inbobwetrust.model.dto;

import com.inbobwetrust.model.entity.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

public class DeliveryStatusDtoTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("[DeliveryStatusDto] Validation 정상")
    void newTest_success1() {
        DeliveryStatusDto deliveryStatusDto =
                DeliveryStatusDto.builder().orderId(1L).orderStatus(OrderStatus.PICKED_UP).build();

        Assertions.assertEquals(0, checkViolations(deliveryStatusDto).size());
    }

    @Test
    @DisplayName("[DeliveryStatusDto] Validation 실패 : 주문상태 누락")
    void newTest_fail1() {
        DeliveryStatusDto deliveryStatusDto =
                DeliveryStatusDto.builder().orderId(1L).orderStatus(null).build();

        Assertions.assertEquals(1, checkViolations(deliveryStatusDto).size());
    }

    @Test
    @DisplayName("[DeliveryStatusDto] Validation 실패 : 주문상태 누락")
    void newTest_fail2() {
        DeliveryStatusDto deliveryStatusDto =
                DeliveryStatusDto.builder().orderId(1L).orderStatus(null).build();

        Assertions.assertEquals(1, checkViolations(deliveryStatusDto).size());
    }

    <T> Set<ConstraintViolation<T>> checkViolations(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        return violations;
    }
}
