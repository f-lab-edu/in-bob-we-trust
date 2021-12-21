package com.inbobwetrust.model.dto;

import com.inbobwetrust.model.entity.OrderStatus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public class DeliveryStatusDtoTest {
    public static final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("[DeliveryStatusDto] Validation 정상")
    void newTest_success1() {
        DeliveryStatusDto deliveryStatusDto =
                DeliveryStatusDto.builder()
                        .orderId(1L)
                        .riderId(1L)
                        .deliveryId(1L)
                        .orderStatus(OrderStatus.PICKED_UP)
                        .build();

        Assertions.assertEquals(0, checkViolations(deliveryStatusDto).size());
    }

    @Test
    @DisplayName("[DeliveryStatusDto] Validation 실패 : 주문상태 누락")
    void newTest_fail1() {
        DeliveryStatusDto deliveryStatusDto = DeliveryStatusDto.builder().orderId(1L).build();

        Assertions.assertEquals(3, checkViolations(deliveryStatusDto).size());
    }

    @Test
    @DisplayName("[DeliveryStatusDto] Validation 실패 : 주문상태 누락")
    void newTest_fail2() {
        DeliveryStatusDto deliveryStatusDto =
                DeliveryStatusDto.builder().orderId(1L).orderStatus(OrderStatus.NEW).build();

        Assertions.assertEquals(2, checkViolations(deliveryStatusDto).size());
    }

    public static <T> Set<ConstraintViolation<T>> checkViolations(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        return violations;
    }
}
