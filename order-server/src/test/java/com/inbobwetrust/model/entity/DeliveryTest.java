package com.inbobwetrust.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.inbobwetrust.service.DeliveryServiceTest;
import com.inbobwetrust.util.vo.DeliveryInstanceGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class DeliveryTest {
    Logger log = (Logger) LoggerFactory.getLogger(DeliveryServiceTest.class);

    @Test
    void deepCopyTest() {
        Delivery original =
                Delivery.builder()
                        .id(1L)
                        .orderId(1L)
                        .riderId(1L)
                        .agencyId(1L)
                        .orderStatus(OrderStatus.COMPLETE)
                        .pickupTime(LocalDateTime.now())
                        .finishTime(LocalDateTime.now().plusMinutes(30))
                        .build();
        Delivery copy = original.deepCopy();

        assertTrue(copy.equals(original) && copy != original);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getOrderId(), copy.getOrderId());
        assertEquals(original.getRiderId(), copy.getRiderId());
        assertEquals(original.getAgencyId(), copy.getAgencyId());
        assertEquals(original.getOrderStatus(), copy.getOrderStatus());
        assertEquals(original.getPickupTime(), copy.getPickupTime());
        assertEquals(original.getFinishTime(), copy.getFinishTime());
        assertEquals(original.getCreatedAt(), copy.getCreatedAt());
        assertEquals(original.getUpdatedAt(), copy.getUpdatedAt());
    }

    @Test
    @DisplayName("[Delivery.isNew] 신규주문인지를 확인한다.")
    void isNewTest() {
        Delivery newDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);
        newDelivery.setOrderStatus(null);
        assertTrue(newDelivery.isNew());

        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.equals(OrderStatus.NEW)) {
                continue;
            }
            newDelivery.setOrderStatus(orderStatus);
            assertTrue(!newDelivery.isNew());
        }
    }

    @Test
    @DisplayName("[Delivery.isValidPickupTime] 픽업요청시간이 현재시간 이후여야한다.")
    void isValidPickupTime() {
        Delivery newDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);

        newDelivery.setPickupTime(LocalDateTime.now().minusSeconds(1));
        assertTrue(!newDelivery.isValidPickupTime());

        newDelivery.setPickupTime(LocalDateTime.now());
        assertTrue(!newDelivery.isValidPickupTime());

        newDelivery.setPickupTime(LocalDateTime.now().plusMinutes(1));
        assertTrue(newDelivery.isValidPickupTime());
    }

    @Test
    @DisplayName("[Delivery.matchesRider]성공 : 라이더정보(id & agencyId) 비교")
    void matchesRiderTest1() {
        Delivery aDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);
        Rider rider =
                Rider.builder()
                        .id(aDelivery.getRiderId())
                        .agencyId(aDelivery.getAgencyId())
                        .build();

        assertTrue(aDelivery.matchesRider(rider));
    }

    @Test
    @DisplayName("[Delivery.matchesRider]실패 : 라이더정보(id & agencyId) 비교")
    void matchesRiderTest2() {
        Delivery aDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);

        assertTrue(!aDelivery.matchesRider(null));

        assertTrue(
                !aDelivery.matchesRider(
                        Rider.builder()
                                .id(aDelivery.getRiderId() + 1)
                                .agencyId(aDelivery.getAgencyId())
                                .build()));

        assertTrue(
                !aDelivery.matchesRider(
                        Rider.builder()
                                .id(aDelivery.getRiderId())
                                .agencyId(aDelivery.getAgencyId() + 1)
                                .build()));

        assertTrue(
                !aDelivery.matchesRider(
                        Rider.builder()
                                .id(aDelivery.getRiderId() + 1)
                                .agencyId(aDelivery.getAgencyId() + 1)
                                .build()));
    }

    @Test
    @DisplayName("[Delivery.matchesRider]성공 : 라이더정보(id & agencyId) 비교")
    void matchesRiderTest3() {
        Delivery aDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);
        Rider rider = Rider.builder().id(null).agencyId(null).build();

        assertTrue(!aDelivery.matchesRider(rider));
        assertTrue(!aDelivery.matchesRider(null));
    }

    @Test
    @DisplayName("[Delivery.canSetRider] ACCEPTED 주문상태에서만 라이더 배정가능한 상태")
    void canSetRiderTest() {
        Delivery aDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);
        for (OrderStatus status : OrderStatus.values()) {
            aDelivery.setOrderStatus(status);
            assertTrue(
                    status.equals(OrderStatus.ACCEPTED)
                            ? aDelivery.canSetRider()
                            : !aDelivery.canSetRider());
        }
    }

    @Test
    @DisplayName("[Delivery.canSetRider] 배달완료시간이 설정된 상태")
    void canSetRiderTest2() {
        Delivery aDelivery = DeliveryInstanceGenerator.makeDeliveryForRequestAndResponse().get(0);

        aDelivery.setOrderStatus(OrderStatus.ACCEPTED);
        aDelivery.setFinishTime(LocalDateTime.now().plusMinutes(30));
        assertTrue(aDelivery.canSetRider());

        aDelivery.setFinishTime(null);
        assertFalse(aDelivery.canSetRider());
    }
}
