package com.inbobwetrust.model.dto;

import static com.inbobwetrust.model.dto.DeliveryStatusDtoTest.checkViolations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DeliveryGetStatusDtoTest {
    @Test
    @DisplayName("[DeliveryGetStatusDto] Validation 정상")
    void newTest_success1() {
        DeliveryGetStatusDto deliveryGetStatusDto =
                DeliveryGetStatusDto.builder().orderId(1L).build();

        Assertions.assertEquals(0, checkViolations(deliveryGetStatusDto).size());
    }

    @Test
    @DisplayName("[DeliveryGetStatusDto] Validation 실패 : 주문번호 누락")
    void newTest_fail1() {
        DeliveryGetStatusDto deliveryGetStatusDto = DeliveryGetStatusDto.builder().build();

        Assertions.assertEquals(1, checkViolations(deliveryGetStatusDto).size());
    }
}
