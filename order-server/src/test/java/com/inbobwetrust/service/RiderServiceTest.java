package com.inbobwetrust.service;

import static org.mockito.Mockito.*;

import com.inbobwetrust.model.entity.Rider;
import com.inbobwetrust.model.entity.RiderLocation;
import com.inbobwetrust.repository.RiderLocationRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RiderServiceTest {

    @InjectMocks RiderLocationService riderLocationService;

    @Mock RiderLocationRepository riderLocationRepository;

    @Test
    @DisplayName("라이더 위치 업데이트 : 성공")
    void putIfAbsentLocation_successTest() {
        Rider rider = Rider.builder().id(1L).build();
        RiderLocation riderLocation =
                RiderLocation.builder().riderId(rider.getId()).latitude(1.0).longitude(2.0).build();
        when(riderLocationRepository.putIfAbsentLocation(any())).thenReturn(1);

        RiderLocation updateLocation = riderLocationService.updateLocation(riderLocation);

        verify(riderLocationRepository, times(1)).putIfAbsentLocation(any());
    }
}
