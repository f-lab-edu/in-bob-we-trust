package com.inbobwetrust.model.mapper;

import com.inbobwetrust.model.dto.RiderLocationDto;
import com.inbobwetrust.model.entity.RiderLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class RiderMapperTest {
    @Autowired RiderMapper riderMapper;

    @Test
    @DisplayName("[RiderMapper.toLocationEntity]")
    void toLocationEntity() {
        RiderLocationDto expected =
                RiderLocationDto.builder()
                        .riderId(1L)
                        .latitude(RiderLocation.MAX_LATITUDE)
                        .longitude(RiderLocation.MAX_LONGITUDE)
                        .build();

        RiderLocation actual = riderMapper.toLocationEntity(expected);

        Assertions.assertEquals(expected.getRiderId(), actual.getRiderId());
        Assertions.assertEquals(expected.getLatitude(), actual.getLatitude());
        Assertions.assertEquals(expected.getLongitude(), actual.getLongitude());
    }
}
