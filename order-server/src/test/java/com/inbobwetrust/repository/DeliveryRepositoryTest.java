package com.inbobwetrust.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.inbobwetrust.exceptions.EmptyResultSetSqlException;
import com.inbobwetrust.model.dto.DeliveryStatusDto;
import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.entity.Order;

import org.apache.ibatis.executor.result.ResultMapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
class DeliveryRepositoryTest {

    @Autowired DeliveryRepository deliveryRepository;
    @Autowired OrderRepository orderRepository;

    private Delivery LAST_DELIVERY;
    List<Delivery> dels;

    Comparator<Delivery> descendingByIdComparator = (o1, o2) -> (int) (o2.getId() - o1.getId());

    @BeforeEach
    void setUp() {
        dels = deliveryRepository.findAll();
        LAST_DELIVERY = dels.get(dels.size() - 1);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("[DeliveryRepository.save] 성공")
    void saveTest_success() {
        Delivery newDelivery = makeFrom(LAST_DELIVERY);
        int expected = dels.size() + 1;

        assertDoesNotThrow(() -> deliveryRepository.save(newDelivery));
        int actual = deliveryRepository.findAll().size();
        assertEquals(expected, actual);
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("[DeliveryRepository.save] 실패 : 주문번호 누락")
    void saveTest_fail1() {
        Delivery newDelivery = makeFrom(LAST_DELIVERY);
        newDelivery.setOrderId(null);

        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.save(newDelivery));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("[DeliveryRepository.save] 실패 : 주문상ㅗ태 누락")
    void saveTest_fail2() {
        Delivery newDelivery = makeFrom(LAST_DELIVERY);
        newDelivery.setOrderStatus(null);

        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.save(newDelivery));
    }

    private Delivery makeFrom(Delivery ref) {
        Order sampleOrder = orderRepository.findAll().get(0);
        orderRepository.save(sampleOrder);

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
    @org.junit.jupiter.api.Order(5)
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
    @org.junit.jupiter.api.Order(6)
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
    @org.junit.jupiter.api.Order(7)
    @DisplayName("[DeliveryRepository.update] 성공")
    void updateTest_success() {
        Delivery expected = deliveryRepository.findAll().get(0);
        expected.setOrderStatus(expected.getOrderStatus().returnSomethingElse());

        deliveryRepository.update(expected);

        Delivery actual =
                deliveryRepository
                        .findById(expected.getOrderId())
                        .orElseThrow(EmptyResultSetSqlException::new);

        assertNotEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getOrderStatus(), actual.getOrderStatus());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    @DisplayName("[DeliveryRepository.findDeliveryStatusByOrderId] 성공")
    void findDeliveryStatusByOrderId_success() {
        List<Delivery> existingList = deliveryRepository.findAll();
        DeliveryStatusDto expected = existingList.get(existingList.size() - 1).toDeliveryStatus();

        DeliveryStatusDto actual =
                deliveryRepository
                        .findDeliveryStatusByOrderId(expected.getOrderId())
                        .orElseThrow(EmptyResultSetSqlException::new);
        assertEquals(expected, actual);
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    @DisplayName("[라이더배정] 존재하지 않는 주문번호")
    void saveAndUpdateFail_orderIdNotExists() {
        // Setup 1 : 미존재 주문전호
        Long notExistsOrderId = Long.MAX_VALUE;
        checkNoSuchOrderWithId(notExistsOrderId);
        // Setup 2 : UPDATE 가능 Delivery에 미존재 주문번호 추가
        Delivery aDelivery = deliveryRepository.findAll().get(0);
        assertDoesNotThrow(() -> deliveryRepository.update(aDelivery));
        aDelivery.setOrderId(notExistsOrderId);
        // Execute & Assert
        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.save(aDelivery));
        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.update(aDelivery));
    }

    private void checkNoSuchOrderWithId(Long notExistsOrderId) {
        boolean orderIdDoesNotExist =
                orderRepository.findAll().stream()
                        .noneMatch(
                                order -> order.getId().longValue() == notExistsOrderId.longValue());
        assertTrue(orderIdDoesNotExist);
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    @DisplayName("[라이더배정] 존재하지 않는 라이더ID")
    void updateTest_riderIdNotExists() {
        // Setup 1 : 미존재 라이더 번호, 설정기준은 "classpath:data-test-h2.sql" 참고
        Long PRESET_NOTEXIST_RIDERID = Long.MAX_VALUE;
        // Setup 2 : UPDATE 가능 Delivery에 미존재 라이더ID 추가
        Delivery aDelivery = deliveryRepository.findAll().get(0);
        aDelivery.setFinishTime(aDelivery.getFinishTime().plusMinutes(1));
        assertDoesNotThrow(() -> deliveryRepository.update(aDelivery));
        aDelivery.setRiderId(PRESET_NOTEXIST_RIDERID);
        // Execute & Assert
        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.save(aDelivery));
        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.update(aDelivery));
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    @DisplayName("[라이더배정] 존재하지 않는 배달대행사 ID")
    void updateTest_agencyIdNotExists() {
        // Setup 1 : 미존재 배달대행사 번호, 설정기준은 "classpath:data-test-h2.sql" 참고
        Long PRESET_NOTEXIST_AGENCY = Long.MAX_VALUE;
        // Setup 2 : UPDATE 가능 Delivery에 미존재 배달대행사ID 추가
        Delivery aDelivery = deliveryRepository.findAll().get(0);
        aDelivery.setFinishTime(aDelivery.getFinishTime().plusMinutes(1));
        assertDoesNotThrow(() -> deliveryRepository.update(aDelivery));
        aDelivery.setRiderId(PRESET_NOTEXIST_AGENCY);
        // Execute & Assert
        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.save(aDelivery));
        assertThrows(
                DataIntegrityViolationException.class, () -> deliveryRepository.update(aDelivery));
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    @DisplayName("[라이더배정] UPDATE SQL 아무것도 되지 않음")
    void updateTest_nothingHappened() {
        // Setup
        List<Delivery> deliveryList = deliveryRepository.findAll();
        Collections.sort(deliveryList, descendingByIdComparator);
        Delivery delivery = deliveryList.get(0);
        // Execute
        boolean expectedTrue = deliveryRepository.update(delivery);
        delivery.setId(delivery.getId() + 1);
        boolean expectedFalse = deliveryRepository.update(delivery);
        // Assert
        assertTrue(expectedTrue);
        assertFalse(expectedFalse);
    }
}
