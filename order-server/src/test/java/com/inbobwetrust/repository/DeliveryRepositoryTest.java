package com.inbobwetrust.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.inbobwetrust.exceptions.EmptyResultSetSqlException;
import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.entity.DeliveryStatus;

import org.apache.ibatis.executor.result.ResultMapException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
class DeliveryRepositoryTest {

    @Autowired DeliveryRepository deliveryRepository;
    @Autowired RiderRepository riderRepository;

    private static final Long PRESET_RIDER_ID = 1L;
    private Delivery LAST_DELIVERY;
    List<Delivery> dels;

    @BeforeEach
    void setUp() {
        dels = deliveryRepository.findAll();
        LAST_DELIVERY = dels.get(dels.size() - 1);
    }

    @Test
    @DisplayName("[DeliveryRepository.save] 성공")
    void saveTest_success() {
        Delivery newDelivery = makeFrom(LAST_DELIVERY);
        int expected = dels.size() + 1;

        assertDoesNotThrow(() -> deliveryRepository.save(newDelivery));
        int actual = deliveryRepository.findAll().size();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("[DeliveryRepository.save] 실패 : 주문번호 누락")
    void saveTest_fail1() {
        Delivery newDelivery = makeFrom(LAST_DELIVERY);
        newDelivery.setOrderId(null);

        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.save(newDelivery));
    }

    @Test
    @DisplayName("[DeliveryRepository.save] 실패 : 주문상ㅗ태 누락")
    void saveTest_fail2() {
        Delivery newDelivery = makeFrom(LAST_DELIVERY);
        newDelivery.setOrderStatus(null);

        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.save(newDelivery));
    }

    private Delivery makeFrom(Delivery ref) {
        return Delivery.builder()
                .orderId(ref.getOrderId() + 1)
                .riderId(ref.getRiderId())
                .agencyId(ref.getAgencyId())
                .orderStatus(ref.getOrderStatus())
                .pickupTime(ref.getPickupTime())
                .finishTime(ref.getFinishTime())
                .build();
    }

    @Test
    @DisplayName("[DeliveryRepository.findAll] 성공 : 10회 추가저장")
    void findAllTest_success() {
        int cnt = 10;
        for (int i = 0; i < cnt; i++) {
            List<Delivery> existingList = deliveryRepository.findAll();
            int expected = existingList.size() + 1;
            Delivery lastDelivery = existingList.get(existingList.size() - 1);

            assertDoesNotThrow(() -> deliveryRepository.save(makeFrom(lastDelivery)));
            int actual = deliveryRepository.findAll().size();
            assertEquals(expected, actual);
        }
    }

    @Test
    @DisplayName("[DeliveryRepository.findByOrderId] 성공")
    void findByOrderId_success() {
        List<Delivery> existingList = deliveryRepository.findAll();
        Delivery expected = existingList.get(existingList.size() - 1);

        Delivery actual =
                deliveryRepository
                        .findByOrderId(expected.getOrderId())
                        .orElseThrow(ResultMapException::new);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("[DeliveryRepository.update] 성공")
    void updateTest_success() {
        List<Delivery> existingList = deliveryRepository.findAll();
        Delivery original = existingList.get(existingList.size() - 1);
        Delivery expected = makeChangedCopy(original.deepCopy());

        assertDoesNotThrow(() -> deliveryRepository.update(expected));
        assertEquals(existingList.size(), deliveryRepository.findAll().size());
        var temp = deliveryRepository.findAll();
        Delivery actual =
                deliveryRepository
                        .findByOrderId(expected.getOrderId())
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getOrderId(), expected.getOrderId());
        assertEquals(actual.getRiderId(), expected.getRiderId());
        assertEquals(actual.getAgencyId(), expected.getAgencyId());
        assertEquals(actual.getOrderStatus(), expected.getOrderStatus());
        assertEquals(actual.getPickupTime(), expected.getPickupTime());
        assertEquals(actual.getFinishTime(), expected.getFinishTime());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertNotEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

    private Delivery makeChangedCopy(Delivery copy) {
        copy.setOrderStatus(copy.getOrderStatus().returnSomethingElse());
        copy.setPickupTime(copy.getPickupTime().plusMinutes(1));
        copy.setFinishTime(copy.getFinishTime().plusMinutes(1));
        return copy;
    }

    @Test
    @DisplayName("[DeliveryRepository.findDeliveryStatusByOrderId] 성공")
    void findDeliveryStatusByOrderId_success() {
        List<Delivery> existingList = deliveryRepository.findAll();
        DeliveryStatus original = existingList.get(existingList.size() - 1).toDeliveryStatus();

        DeliveryStatus actual =
                deliveryRepository
                        .findDeliveryStatusByOrderId(original.getOrderId())
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertEquals(original, actual);
    }
}
