package com.inbobwetrust.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryTest {
  @Test
  void deepCopyTest() {
    // given
    var delivery = makeValidDelivery();
    // when
    var copy = delivery.deepCopy();
    // then
    assertTrue(delivery != copy);
    assertEquals(delivery, copy);
    assertEquals(delivery.hashCode(), copy.hashCode());
  }

  @Test
  void copyTimeFieldsTest() {
    // given
    var delivery1 = makeValidDelivery();
    var delivery2 = delivery1.deepCopy();
    assertTrue(delivery1 != delivery2);
    assertEquals(delivery1, delivery2);

    delivery1.setOrderTime(delivery1.getOrderTime().plusNanos(1));
    delivery1.setPickupTime(delivery1.getPickupTime().plusNanos(1));
    delivery1.setFinishTime(delivery1.getFinishTime().plusNanos(1));
    assertNotEquals(delivery1, delivery2);
    assertNotEquals(delivery1.getFinishTime(), delivery2.getFinishTime());
    assertNotEquals(delivery1.getOrderTime(), delivery2.getOrderTime());
    assertNotEquals(delivery1.getPickupTime(), delivery2.getPickupTime());

    // when
    delivery1.copyTimeFields(delivery2);
    // then
    assertEquals(delivery1, delivery2);
  }

  private Delivery makeValidDelivery() {
    return Delivery.builder()
        .id("id-1234")
        .orderId("order1")
        .riderId("rider-1234")
        .agencyId("agency-1234")
        .customerId("customer-1234")
        .address("서울시 강남구...")
        .phoneNumber("01031583977")
        .deliveryStatus(DeliveryStatus.ACCEPTED)
        .orderTime(LocalDateTime.now().minusMinutes(1))
        .pickupTime(LocalDateTime.now().plusMinutes(30))
        .finishTime(LocalDateTime.now().plusMinutes(60))
        .build();
  }
}
