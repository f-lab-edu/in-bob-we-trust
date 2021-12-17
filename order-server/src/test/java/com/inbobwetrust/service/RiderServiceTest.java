package com.inbobwetrust.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inbobwetrust.model.entity.Location;
import com.inbobwetrust.model.entity.Rider;
import com.inbobwetrust.repository.RiderRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RiderServiceTest {

    @InjectMocks RiderService riderService;

    @Mock RiderRepository riderRepository;

    @Test
    @DisplayName("라이더 위치 업데이트 : 성공")
    void updateLocation_successTest() {
        Rider rider = Rider.builder().id(1L).location(new Location(1, 2)).build();
        when(riderRepository.update(rider)).thenReturn(true);
        when(riderRepository.findByRiderId(rider.getId())).thenReturn(Optional.of(rider));

        Rider updatedRider = riderService.updateLocation(rider);

        verify(riderRepository, times(1)).update(rider);
    }

}
