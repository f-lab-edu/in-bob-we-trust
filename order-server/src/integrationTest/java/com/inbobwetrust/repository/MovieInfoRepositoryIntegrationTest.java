package com.inbobwetrust.repository;

import com.inbobwetrust.domain.Delivery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

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

  @Test
  void findByOrderIdContaining() {
    // Arrange
    int TOTAL_SIZE = 100;
    List<Delivery> deliveryList = new ArrayList<>();
    for (int i = 1; i <= TOTAL_SIZE; i++) {
      Delivery delivery = makeValidDelivery();
      delivery.setOrderId("order-" + i);
      deliveryList.add(delivery);
      deliveryRepository.saveAll(deliveryList).blockLast();
    }
    // Stub
    // Act
    Set<String> savedOrderIds = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      int page = i;
      int size = 10;
      var pageable = PageRequest.of(page, size);
      var countingStream = deliveryRepository.findAllByOrderIdContaining("", pageable).log();
      var orderIdStream = deliveryRepository.findAllByOrderIdContaining("", pageable).log();
      // Assert
      StepVerifier.create(countingStream).expectNextCount(10).verifyComplete();
      StepVerifier.create(orderIdStream)
          .thenConsumeWhile(delivery -> true, delivery -> savedOrderIds.add(delivery.getOrderId()))
          .verifyComplete();
    }
    Assertions.assertEquals(TOTAL_SIZE, savedOrderIds.size());
  }
}
