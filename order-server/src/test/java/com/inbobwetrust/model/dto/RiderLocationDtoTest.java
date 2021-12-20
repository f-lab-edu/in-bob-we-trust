package com.inbobwetrust.model.dto;

import com.inbobwetrust.model.entity.RiderLocation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public class RiderLocationDtoTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("[RiderDto] Validation 정상 : MAX longitude && MAX latitude")
    void newTest_success1() {
        RiderLocationDto riderLocationDto =
                RiderLocationDto.builder()
                        .riderId(1L)
                        .longitude(RiderLocation.MAX_LONGITUDE)
                        .latitude(RiderLocation.MAX_LATITUDE)
                        .build();

        Set<ConstraintViolation<RiderLocationDto>> violations =
                validator.validate(riderLocationDto);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("[RiderDto] Validation 정상통과 : MIN longitude & MIN latitude")
    void newTest_success2() {
        RiderLocationDto riderLocationDto =
                RiderLocationDto.builder()
                        .riderId(1L)
                        .longitude(RiderLocation.MIN_LONGITUDE)
                        .latitude(RiderLocation.MIN_LATITUDE)
                        .build();

        Set<ConstraintViolation<RiderLocationDto>> violations =
                validator.validate(riderLocationDto);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("[RiderDto] Validation 정상통과 : MIN longitude & MIN latitude")
    void newTest_success3() {
        RiderLocationDto riderLocationDto = RiderLocationDto.builder().build();

        Set<ConstraintViolation<RiderLocationDto>> violations =
                validator.validate(riderLocationDto);
        Assertions.assertEquals(3, violations.size());
    }

    @Test
    @DisplayName("[RiderDto] Validation 실패 : 초과 MIN longitude & MIN latitude")
    void newTest_fail2() {
        RiderLocationDto riderLocationDto =
                RiderLocationDto.builder()
                        .riderId(1L)
                        .longitude(RiderLocation.MIN_LONGITUDE - 0.00001)
                        .latitude(RiderLocation.MIN_LATITUDE - 0.000001)
                        .build();

        Set<ConstraintViolation<RiderLocationDto>> violations =
                validator.validate(riderLocationDto);
        Assertions.assertEquals(2, violations.size());
    }

    @Test
    @DisplayName("[RiderDto] Validation 실패 : 초과 MAX longitude & MAX latitude")
    void newTest_fail3() {
        RiderLocationDto riderLocationDto =
                RiderLocationDto.builder()
                        .riderId(1L)
                        .longitude(RiderLocation.MAX_LONGITUDE + 0.00001)
                        .latitude(RiderLocation.MAX_LATITUDE + 0.000001)
                        .build();

        Set<ConstraintViolation<RiderLocationDto>> violations =
                validator.validate(riderLocationDto);
        Assertions.assertEquals(2, violations.size());
    }
}
