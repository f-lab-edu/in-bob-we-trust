package com.inbobwetrust.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RiderLocationServiceTest {

  @InjectMocks RiderLocationService locationService;

  @Mock RiderLocationRepository locationRepository;

  @Mock DeliveryRepository deliveryRepository;

  @ParameterizedTest
  @DisplayName("캐시에 저장하는 워크플로우")
  @MethodSource("operations")
  void setIfPresent_fail_does_not_exist(
      boolean canSetIfPresent,
      boolean isPickedUp,
      boolean canSetIfAbsent,
      boolean finalResult,
      int isPickedUpTimes,
      int setIfAbsentTimes) {
    // given
    var riderLocation = new RiderLocation("rider-1234", "delivery-1234", 23.0f, Float.MIN_VALUE);

    // when
    when(locationRepository.setIfPresent(any(RiderLocation.class)))
        .thenReturn(Mono.just(canSetIfPresent));

    if (!canSetIfPresent) {
      when(deliveryRepository.isPickedUp(anyString())).thenReturn(Mono.just(isPickedUp));
    }

    if (isPickedUp && !canSetIfPresent) {
      when(locationRepository.setIfAbsent(any(RiderLocation.class)))
          .thenReturn(Mono.just(canSetIfAbsent));
    }
    // then
    var resultStream = locationService.setIfPresent(riderLocation);

    StepVerifier.create(resultStream).expectNextMatches(res -> res == finalResult).verifyComplete();

    verify(locationRepository, times(1)).setIfPresent(any(RiderLocation.class));
    verify(deliveryRepository, times(isPickedUpTimes)).isPickedUp(anyString());
    verify(locationRepository, times(setIfAbsentTimes)).setIfAbsent(any(RiderLocation.class));
  }

  static Stream<Arguments> operations() {
    return Stream.of(
        Arguments.of(true, true, true, true, 0, 0),
        Arguments.of(true, false, false, true, 0, 0),
        Arguments.of(false, true, true, true, 1, 1),
        Arguments.of(false, true, false, false, 1, 1),
        Arguments.of(false, false, true, false, 1, 0),
        Arguments.of(false, false, false, false, 1, 0));
  }

  @Test
  void findAllTest() {
    // given
    var locations =
        List.of(
            new RiderLocation("rider-1234", "delivery-1234", 23.0f, Float.MIN_VALUE),
            new RiderLocation("rider-12222", "delivery-12222", 23.0f, Float.MIN_VALUE));
    var returnValue = Flux.fromIterable(locations);
    // when
    when(locationRepository.findAll()).thenReturn(returnValue);
    // then
    StepVerifier.create(locationService.findAll())
        .expectNext(locations.get(0), locations.get(1))
        .verifyComplete();
  }
}
