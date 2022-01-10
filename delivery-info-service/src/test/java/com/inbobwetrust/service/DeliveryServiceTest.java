package com.inbobwetrust.service;

import com.inbobwetrust.publisher.DeliveryPublisher;
import com.inbobwetrust.repository.primary.PrimaryDeliveryRepository;
import com.inbobwetrust.repository.secondary.SecondaryDeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

  @InjectMocks DeliveryServiceImpl deliveryService;
  @Mock PrimaryDeliveryRepository primaryDeliveryRepository;
  @Mock SecondaryDeliveryRepository secondaryDeliveryRepository;
  @Mock DeliveryPublisher deliveryPublisher;

  @Test
  void fieldValuesTest() {
    Assertions.assertEquals(3L, DeliveryService.MAX_ATTEMPTS);
    Assertions.assertEquals(Duration.ofMillis(500), DeliveryService.FIXED_DELAY);
  }

  @Test
  void defaultRetryBackoffSpecTest() {
    // given
    // when
    var retryPolicy =
        Mono.error(new IllegalStateException())
            .retryWhen(deliveryService.defaultRetryBackoffSpec());
    // then
    StepVerifier.create(retryPolicy)
        .expectErrorSatisfies(e -> assertThat(e).isInstanceOf(IllegalStateException.class))
        .verify();
  }
}
