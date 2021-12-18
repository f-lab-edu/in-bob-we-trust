package com.inbobwetrust.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.inbobwetrust.exceptions.EmptyResultSetSqlException;
import com.inbobwetrust.model.entity.Rider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
public class RiderRepositoryTest {
    @Autowired RiderRepository riderRepository;

    private static final Long PRESET_AGENCY_ID = 1L;
    Long nonExistentAgencyId = -9999L;

    @BeforeEach
    @DisplayName("[RiderRepositoryTest.setUp] 설명 : 모든 데이터 삭제")
    void setUp() {}

    public static Rider makeRider() {
        return Rider.builder().agencyId(PRESET_AGENCY_ID).build();
    }

    @Test
    @DisplayName("[RiderRepository.save] 성공 : 존재하는 배달대행사")
    void saveTest_success() {
        int expected = riderRepository.findAll().size() + 1;

        assertDoesNotThrow(() -> riderRepository.save(makeRider()));
        int actual = riderRepository.findAll().size();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("[RiderRepository.save] 실패 : 동일한 아이디 명시해서 저장")
    void saveTest_fail1() {
        Rider rider = getLastRider();

        assertThrows(DataIntegrityViolationException.class, () -> riderRepository.save(rider));
    }

    private Rider getLastRider() {
        List<Rider> riders = riderRepository.findAll();
        return riders.get(riders.size() - 1);
    }

    @Test
    @DisplayName("[RiderRepository.save] 실패 : ID만 명시")
    void saveTest_fail2() {
        Rider lastRider = getLastRider();
        Rider rider = Rider.builder().id(lastRider.getId()).build();

        assertThrows(DataIntegrityViolationException.class, () -> riderRepository.save(rider));
    }

    @Test
    @DisplayName("[RiderRepository.save] 실패 : 전부 미입력")
    void saveTest_fail_noInformation() {
        Rider rider = Rider.builder().build();

        assertThrows(DataIntegrityViolationException.class, () -> riderRepository.save(rider));
    }

    @Test
    @DisplayName("[RiderRepository.save] 실패 : 존재하지 않는 배달대행사")
    void saveTest_fail_noSuchAgency() {
        Rider rider = Rider.builder().agencyId(nonExistentAgencyId).build();

        assertThrows(DataIntegrityViolationException.class, () -> riderRepository.save(rider));
    }

    @Test
    @DisplayName("[RiderRepository.findAll] 성공 : 모든 라이더 정보를 가져온다.")
    void findAllTest_success() {
        int addedRows = 10;
        int expected = riderRepository.findAll().size() + 10;
        for (int i = 0; i < addedRows; i++) {
            riderRepository.save(makeRider());
        }

        int actual = riderRepository.findAll().size();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("[RiderRepository.findByRiderId] 성공 : 라이더 정보 가져오기")
    void findByRiderIdTest_success() {
        List<Rider> allRiders = riderRepository.findAll();
        Rider expected = allRiders.get(allRiders.size() - 1);

        Rider actual =
                riderRepository
                        .findByRiderId(expected.getId())
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("[RiderRepository.findByRiderId] 실패 : 존재하지 않는 라이더 정보")
    void findByRiderIdTest_fail() {
        List<Rider> allRiders = riderRepository.findAll();
        Rider lastRider = allRiders.get(allRiders.size() - 1);
        final Long nonExistentId = lastRider.getId() + 1L;
        assertTrue(
                allRiders.stream()
                        .noneMatch(
                                rider -> rider.getId().longValue() == nonExistentId.longValue()));

        assertThrows(
                EmptyResultSetSqlException.class,
                () ->
                        riderRepository
                                .findByRiderId(nonExistentId)
                                .orElseThrow(EmptyResultSetSqlException::new));
    }
}
