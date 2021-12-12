package com.inbobwetrust.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inbobwetrust.model.vo.Rider;
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
        Rider rider =
                Rider.builder().orderId("order-1").riderId("rider-1").location("sumplace").build();
        when(riderRepository.update(rider)).thenReturn(true);
        when(riderRepository.findByRiderId(rider.getRiderId())).thenReturn(Optional.of(rider));

        Rider updatedRider = riderService.updateLocation(rider);

        assertEquals(null, updatedRider.getLastUpdated());
        verify(riderRepository, times(1)).update(rider);
    }

    @Test
    @DisplayName("라이더 위치 업데이트 : 실패 (라이더 위치정보, 주문ID, 라이더ID 누락)")
    void updateLocation_failTest() {
        Rider noLocation = Rider.builder().orderId("order-1").riderId("rider-1").build();
        assertThrows(IllegalArgumentException.class, () -> riderService.updateLocation(noLocation));

        Rider noOrderId = Rider.builder().riderId("rider-1").location("home").build();
        assertThrows(IllegalArgumentException.class, () -> riderService.updateLocation(noOrderId));

        Rider noRiderId = Rider.builder().orderId("order-1").location("home").build();
        assertThrows(IllegalArgumentException.class, () -> riderService.updateLocation(noRiderId));

        verify(riderRepository, times(0)).update(any(Rider.class));
        verify(riderRepository, times(0)).findByRiderId(any());
    }
}
