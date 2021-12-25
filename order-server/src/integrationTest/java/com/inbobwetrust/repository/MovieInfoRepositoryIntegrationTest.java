package com.inbobwetrust.repository;

import com.inbobwetrust.domain.Delivery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest // look for repository class, class available no need to  spin up whole context
class MovieInfoRepositoryIntegrationTest {

  @Autowired DeliveryRepository deliveryRepository;

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {
    deliveryRepository.deleteAll().block();
  }

  private Delivery makeValidDelivery() {
    return Delivery.builder()
        .orderId("order-1234")
        .customerId("customer-1234")
        .address("서울시 강남구 삼성동 봉은사로 12-41")
        .phoneNumber("01031583212")
        .orderTime(LocalDateTime.now())
        .build();
  }

  private Delivery makeInvalidDelivery() {
    return Delivery.builder().build();
  }

  @Test
  void save_success() {
    // Arranged
    var expected = makeValidDelivery();
    // Act
    var saved = deliveryRepository.save(expected);
    // Assert
    StepVerifier.create(saved)
        .assertNext(
            actual -> {
              Assertions.assertNotNull(actual.getId());
              Assertions.assertEquals(expected.getCustomerId(), actual.getCustomerId());
              Assertions.assertEquals(expected.getAddress(), actual.getAddress());
              Assertions.assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
              Assertions.assertEquals(expected.getOrderTime(), actual.getOrderTime());
            })
        .verifyComplete();
  }

  @Test
  void save_fail_nullParam() {
    // Arrange
    Delivery expected = null;
    // Act
    // Assert
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> deliveryRepository.save(expected));
  }
}
