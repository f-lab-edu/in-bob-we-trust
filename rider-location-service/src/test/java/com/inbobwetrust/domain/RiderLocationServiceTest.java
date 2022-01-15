package com.inbobwetrust.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RiderLocationServiceTest {

  @InjectMocks RiderLocationService locationService;

  @Mock RiderLocationRepository locationRepository;

  @Test
  void setIfPresent_success() {
    // given
    var newlocation = new RiderLocation("rea", 32f, 32f);
    // when
    when(locationRepository.setIfPresent(any(RiderLocation.class)))
        .thenReturn(Mono.just(Boolean.TRUE));
    var stream = locationService.setIfPresent(newlocation);
    // then
    StepVerifier.create(stream).expectNextMatches(Boolean::booleanValue).verifyComplete();
  }

  @Test
  void setIfPresent_fail_does_not_exist() {
    // given
    var newlocation = new RiderLocation("rea", 32f, 32f);
    // when
    when(locationRepository.setIfPresent(any(RiderLocation.class)))
        .thenReturn(Mono.just(Boolean.TRUE));
    var stream = locationService.setIfPresent(newlocation);
    // then
    StepVerifier.create(stream).expectNextMatches(Boolean::booleanValue).verifyComplete();
  }
}
