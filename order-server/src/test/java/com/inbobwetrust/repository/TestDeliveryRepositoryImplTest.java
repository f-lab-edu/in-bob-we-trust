package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.util.vo.DeliveryInstanceGenerator;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.inbobwetrust.util.vo.DeliveryInstanceGenerator.makeSimpleNumberedDelivery;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TestDeliveryRepositoryImplTest {

    DeliveryRepository deliveryRepository = new TestDeliveryRepositoryImpl();

    @BeforeEach
    void setUp() {
        ((TestDeliveryRepositoryImpl) deliveryRepository).clear();
    }

    @Test
    @DisplayName("Delivery 전체 조회")
    void findAll() {
        assertEquals(0, deliveryRepository.findAll().size());
        int rowCount = 10;

        for (int i = 0; i < rowCount; i++) {
            deliveryRepository.save(
                    Delivery.builder()
                            .orderId("order" + i)
                            .riderId("rider" + i)
                            .wantedPickupTime(LocalDateTime.now())
                            .estimatedDeliveryFinishTime(LocalDateTime.now().plusMinutes(30))
                            .build());
        }

        assertEquals(rowCount, deliveryRepository.findAll().size());
    }

    @Test
    @DisplayName("Delivery 저장")
    void save() {
        Delivery delivery1 = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);

        assertTrue(deliveryRepository.save(delivery1));
        assertFalse(deliveryRepository.save(delivery1));
    }

    @Test
    @DisplayName("Delivery 조회성공 : 주문번호로 찾기")
    void findByOrderId_success() {
        Delivery delivery1 = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);
        deliveryRepository.save(delivery1);

        Optional<Delivery> optionalDelivery =
                deliveryRepository.findByOrderId(delivery1.getOrderId());

        assertTrue(optionalDelivery.isPresent());
        Delivery savedDelivery = optionalDelivery.get();
        assertEquals(savedDelivery.getOrderId(), delivery1.getOrderId());
        assertEquals(savedDelivery.getRiderId(), delivery1.getRiderId());
    }

    @Test
    @DisplayName("Delivery 조회실패 : 존재하지 않는 주문번호로 찾기 ")
    void findByOrderId_fail() {
        assertEquals(0, deliveryRepository.findAll().size());
        Delivery delivery1 = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);

        Optional<Delivery> optionalDelivery =
                deliveryRepository.findByOrderId(delivery1.getOrderId());

        assertTrue(optionalDelivery.isEmpty());
    }

    @Test
    @DisplayName("Delivery Update 성공")
    void update_success() {
        Delivery delivery = makeSimpleNumberedDelivery(1);
        updateSetup(delivery);

        delivery.setRiderId("rider-1");
        assertTrue(deliveryRepository.update(delivery));
        Delivery updatedDelivery = deliveryRepository.findByOrderId(delivery.getOrderId()).get();
        assertEquals("rider-1", updatedDelivery.getRiderId());
    }

    @Test
    @DisplayName("Delivery Update 실패 : 존재하지 않는 아이디")
    void update_fail() {
        Delivery delivery = makeSimpleNumberedDelivery(1);
        updateSetup(delivery);

        assertFalse(deliveryRepository.update(makeSimpleNumberedDelivery(2)));
    }

    private void updateSetup(Delivery delivery) {
        delivery.setRiderId(null);
        deliveryRepository.save(delivery);
        Delivery savedDelivery = deliveryRepository.findByOrderId(delivery.getOrderId()).get();
        assertNull(savedDelivery.getRiderId());
    }
}
