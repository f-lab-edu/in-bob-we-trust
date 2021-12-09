package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Rider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestRiderRepositoryImplTest {

    RiderRepository riderRepository = new TestRiderRepositoryImpl();
    Rider riderToSave;

    @BeforeEach
    void setUp() {
        ((TestRiderRepositoryImpl) riderRepository).clear();
        riderToSave =
                Rider.builder().riderId("rider-1").orderId("order-1").location("somewhere").build();
    }

    @Test
    @DisplayName("라이더 저장 : 성공 & 실패(중복)")
    void save_test() {
        assertTrue(riderRepository.save(riderToSave));
        assertFalse(riderRepository.save(riderToSave));
    }

    @Test
    @DisplayName("라이더 데이터 전체 불러오기")
    void findAll_test() {
        int totalSize = 100;
        assertEquals(0, riderRepository.findAll().size());

        for (int i = 0; i < totalSize; i++) {
            riderRepository.save(
                    Rider.builder()
                            .riderId("rider-" + i)
                            .orderId("order-" + i)
                            .location("somewhere")
                            .build());
        }

        assertEquals(totalSize, riderRepository.findAll().size());
    }

    @Test
    @DisplayName("라이더 위치 업데이트 : 성공")
    void update() {
        assertNull(riderToSave.getLastUpdated());
        riderRepository.save(riderToSave);
        Rider savedRider = riderRepository.findByRiderId(riderToSave.getRiderId()).get();
        assertNotNull(savedRider.getLastUpdated());

        riderRepository.update(
                Rider.builder()
                        .riderId(riderToSave.getRiderId())
                        .orderId(riderToSave.getOrderId())
                        .location(riderToSave.getLocation().concat("-updated"))
                        .build());

        Rider updatedRider = riderRepository.findByRiderId(riderToSave.getRiderId()).get();
        assertNotNull(updatedRider.getLastUpdated());
        assertNotEquals(savedRider.getLastUpdated(), updatedRider.getLastUpdated());
    }

    @Test
    void findByRiderId() {
        riderRepository.save(riderToSave);

        Optional<Rider> savedRider = riderRepository.findByRiderId(riderToSave.getRiderId());

        assertTrue(savedRider.isPresent());
        assertEquals(savedRider.get().getOrderId(), riderToSave.getOrderId());
        assertEquals(savedRider.get().getRiderId(), riderToSave.getRiderId());
        assertEquals(savedRider.get().getLocation(), riderToSave.getLocation());
    }
}
