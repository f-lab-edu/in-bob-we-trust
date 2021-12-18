package com.inbobwetrust.repository;

import static com.inbobwetrust.repository.RiderRepositoryTest.makeRider;

import static org.junit.jupiter.api.Assertions.*;

import com.inbobwetrust.model.entity.Rider;
import com.inbobwetrust.model.entity.RiderLocation;

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
public class RiderLocationRepositoryTest {
    @Autowired RiderLocationRepository locationRepository;
    @Autowired RiderRepository riderRepository;

    private static final Long PRESET_AGENCY_ID = 1L;
    private static final double LONGITUDE_MAX = 180.0;
    private static final double LONGITUDE_MIN = -180.0;
    private static final double LATITUDE_MAX = 90.0;
    private static final double LATITUDE_MIN = -90.0;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
        riderRepository.deleteAll();
        assertLocationTableIsEmpty();
        assertRiderTableIsEmpty();
    }

    private void assertLocationTableIsEmpty() {
        assertEquals(0, locationRepository.findAll().size());
    }

    private void assertRiderTableIsEmpty() {
        assertEquals(0, riderRepository.findAll().size());
    }

    private Rider insertAndGetOneRider() {
        int affectedRows = riderRepository.save(makeRider()); // 라이더 한명 저장
        assertEquals(1, affectedRows);
        List<Rider> riders = riderRepository.findAll();
        return riders.get(riders.size() - 1);
    }

    private RiderLocation makeLocationOfRider(Rider rider) {
        return RiderLocation.builder()
                .riderId(rider.getId())
                .latitude(Double.valueOf(1))
                .longitude(Double.valueOf(2))
                .build();
    }

    @Test
    @DisplayName("[RiderLocationRepository.findByRiderId] 성공")
    void findById_success() {
        RiderLocation location = makeLocationOfRider(insertAndGetOneRider());

        assertDoesNotThrow(() -> locationRepository.putIfAbsentLocation(location));
        RiderLocation saved =
                locationRepository
                        .findByRiderId(location.getRiderId())
                        .orElseThrow(IllegalArgumentException::new);
        assertEquals(location, saved);
    }

    @Test
    @DisplayName("[RiderRepository.findByRiderId] 실패 : 존재하지 않는 라이더")
    void findById_fail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    locationRepository.findByRiderId(1L).orElseThrow(IllegalArgumentException::new);
                });
    }

    @Test
    @DisplayName("[RiderRepository.putIfAbsentLocation] 성공 : 라이더위치 신규 추가")
    void putIfAbsentLocationTest_success_insert() {
        Rider rider = insertAndGetOneRider();
        assertTrue(locationRepository.findByRiderId(rider.getId()).isEmpty());

        assertDoesNotThrow(
                () -> {
                    int affectedRows =
                            locationRepository.putIfAbsentLocation(makeLocationOfRider(rider));
                    assertEquals(1, affectedRows);
                });
    }

    @Test
    @DisplayName("[RiderRepository.putIfAbsentLocation] 성공 : 변경된 라이더위치")
    void putIfAbsentLocationTest_success_update() {
        Rider rider = insertAndGetOneRider();
        RiderLocation location = makeLocationOfRider(rider);

        RiderLocation changedLocationMax =
                RiderLocation.builder()
                        .riderId(location.getRiderId())
                        .latitude(LATITUDE_MAX)
                        .longitude(LONGITUDE_MAX)
                        .build();
        RiderLocation changedLocationMin =
                RiderLocation.builder()
                        .riderId(location.getRiderId())
                        .latitude(LATITUDE_MIN)
                        .longitude(LONGITUDE_MIN)
                        .build();
        assertDoesNotThrow(
                () -> {
                    assertTrue(locationRepository.findByRiderId(rider.getId()).isEmpty());
                    int affectedRowsMin =
                            locationRepository.putIfAbsentLocation(changedLocationMax);
                    assertEquals(1, affectedRowsMin);
                });
        assertDoesNotThrow(
                () -> {
                    int affectedRowsMin =
                            locationRepository.putIfAbsentLocation(changedLocationMin);
                    assertEquals(2, affectedRowsMin);
                });
    }

    @Test
    @DisplayName("[RiderRepository.putIfAbsentLocation] 성공")
    void putIfAbsentLocationTest_success_update2() {
        Rider rider = insertAndGetOneRider();
        RiderLocation location = makeLocationOfRider(rider);
        assertTrue(locationRepository.findByRiderId(rider.getId()).isEmpty());

        assertEquals(1, locationRepository.putIfAbsentLocation(location));
    }

    private void insertNewRiderLocation(Rider rider, RiderLocation location) {
        assertTrue(locationRepository.findByRiderId(rider.getId()).isEmpty());
        int affectedRows = locationRepository.putIfAbsentLocation(location);
        assertEquals(1, affectedRows);
        assertTrue(locationRepository.findByRiderId(rider.getId()).isPresent());
    }

    @Test
    @DisplayName("[RiderRepository.putIfAbsentLocation] 실패 : 정보 전체 누락")
    void putIfAbsentLocationTest_fail1() {
        assertThrows(
                DataIntegrityViolationException.class,
                () -> locationRepository.putIfAbsentLocation(RiderLocation.builder().build()));
    }

    @Test
    @DisplayName("[RiderRepository.putIfAbsentLocation] 실패 : 라이더 ID 누락")
    void putIfAbsentLocationTest_fail2() {
        assertThrows(
                DataIntegrityViolationException.class,
                () -> locationRepository.putIfAbsentLocation(RiderLocation.builder().build()));
    }

    @Test
    @DisplayName("[RiderRepository.putIfAbsentLocation] 실패 : 라이더 ID 존재하지 않음")
    void putIfAbsentLocationTest_fail21() {
        long notExistRiderId = (long) Integer.MAX_VALUE;
        Rider rider = Rider.builder().id(notExistRiderId).agencyId(PRESET_AGENCY_ID).build();
        assertTrue(riderRepository.findByRiderId(rider.getId()).isEmpty());
        RiderLocation location = makeLocationOfRider(Rider.builder().id(notExistRiderId).build());

        assertThrows(
                DataIntegrityViolationException.class,
                () -> locationRepository.putIfAbsentLocation(location));
    }

    @Test
    @DisplayName(
            "[RiderRepository.putIfAbsentLocation] 실패 : 범위초과 [허용된 범위는 각 Longitude(-180 ~ 180),"
                    + " Latitude(-90 ~ 90]")
    void putIfAbsentLocationTest_fail3() {
        Rider rider = insertAndGetOneRider();
        RiderLocation location = makeLocationOfRider(rider);
        insertNewRiderLocation(rider, location);
        List<RiderLocation> invalidLocationList = makeInvalidLatOrLongLocations(rider, location);

        for (RiderLocation invalidLocation : invalidLocationList) {
            assertThrows(
                    DataIntegrityViolationException.class,
                    () -> locationRepository.putIfAbsentLocation(invalidLocation));
        }
    }

    private List<RiderLocation> makeInvalidLatOrLongLocations(Rider rider, RiderLocation location) {
        return List.of(
                new RiderLocation(rider.getId(), LONGITUDE_MAX + 0.00001, LATITUDE_MAX),
                new RiderLocation(rider.getId(), LONGITUDE_MIN - 0.00001, LATITUDE_MIN),
                new RiderLocation(rider.getId(), LONGITUDE_MAX, LATITUDE_MAX + 0.00001),
                new RiderLocation(rider.getId(), LONGITUDE_MIN, LATITUDE_MIN - 0.00001));
    }

    @Test
    @DisplayName("[RiderRepository.putIfAbsentLocation] 실패 : Longitude 누락, 또는 Latitude 누락")
    void putIfAbsentLocationTest_fail4() {
        Rider rider = insertAndGetOneRider();
        RiderLocation location = makeLocationOfRider(rider);
        insertNewRiderLocation(rider, location);

        RiderLocation noLong =
                RiderLocation.builder().riderId(rider.getId()).latitude(LATITUDE_MAX).build();
        assertThrows(
                DataIntegrityViolationException.class,
                () -> locationRepository.putIfAbsentLocation(noLong));
        RiderLocation noLat =
                RiderLocation.builder().riderId(rider.getId()).longitude(LONGITUDE_MAX).build();
        assertThrows(
                DataIntegrityViolationException.class,
                () -> locationRepository.putIfAbsentLocation(noLat));
    }
}
