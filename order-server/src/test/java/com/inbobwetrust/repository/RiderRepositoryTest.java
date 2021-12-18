package com.inbobwetrust.repository;

import com.inbobwetrust.exceptions.EmptyResultSetException;
import com.inbobwetrust.model.entity.Rider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class RiderRepositoryTest {
    @Autowired RiderRepository riderRepository;

    private static final Long PRESET_AGENCY_ID = 1L;
    Long nonExistentAgencyId = -9999L;

    @BeforeEach
    @DisplayName("[RiderRepositoryTest.setUp] 설명 : 모든 데이터 삭제")
    void setUp() {
        riderRepository.deleteAll();
    }

    private void assertRiderTableIsEmpty() {
        assertEquals(0, riderRepository.findAll().size());
    }

    public static Rider makeRider() {
        return Rider.builder().agencyId(PRESET_AGENCY_ID).build();
    }

    @Test
    @DisplayName("[RiderRepository.deleteAll] 성공 : 전부삭제")
    void deleteAll_success() {
        assertRiderTableIsEmpty();
        int count = 10;
        for (int i = 0; i < count; i++) riderRepository.save(makeRider());
        assertEquals(count, riderRepository.findAll().size());

        riderRepository.deleteAll();
        assertEquals(0, riderRepository.findAll().size());
    }

    @Test
    @DisplayName("[RiderRepository.save] 성공 : 존재하는 배달대행사")
    void saveTest_success() {
        assertRiderTableIsEmpty();

        assertDoesNotThrow(() -> riderRepository.save(makeRider()));
        assertEquals(1, riderRepository.findAll().size());
    }

    @Test
    @DisplayName("[RiderRepository.save] 실패 : 동일한 아이디 명시해서 저장")
    void saveTest_fail1() {
        assertRiderTableIsEmpty();
        Rider rider = Rider.builder().id(1L).agencyId(PRESET_AGENCY_ID).build();

        assertDoesNotThrow(() -> riderRepository.save(rider));
        assertThrows(DataIntegrityViolationException.class, () -> riderRepository.save(rider));
    }

    @Test
    @DisplayName("[RiderRepository.save] 실패 : ID만 명시")
    void saveTest_fail2() {
        assertRiderTableIsEmpty();
        Rider rider = Rider.builder().id(1L).build();

        assertThrows(DataIntegrityViolationException.class, () -> riderRepository.save(rider));
    }

    @Test
    @DisplayName("[RiderRepository.save] 실패 : 전부 미입력")
    void saveTest_fail_noInformation() {
        assertRiderTableIsEmpty();
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
        assertRiderTableIsEmpty();
        int tableSize = 10;
        for (int i = 0; i < tableSize; i++) {
            riderRepository.save(makeRider());
        }

        List<Rider> riders = riderRepository.findAll();
        assertEquals(tableSize, riders.size());
    }

    @Test
    @DisplayName("[RiderRepository.findByRiderId] 성공 : 라이더 정보 가져오기")
    void findByRiderIdTest_success() {
        assertRiderTableIsEmpty();
        int affectedRows = riderRepository.save(makeRider());
        assertEquals(1, affectedRows);

        assertEquals(1, riderRepository.findAll().size());
    }

    @Test
    @DisplayName("[RiderRepository.findByRiderId] 실패 : 존재하지 않는 라이더 정보")
    void findByRiderIdTest_fail() {
        assertRiderTableIsEmpty();

        assertThrows(
                IllegalArgumentException.class,
                () -> riderRepository.findByRiderId(1L).orElseThrow(IllegalArgumentException::new));
    }
}
