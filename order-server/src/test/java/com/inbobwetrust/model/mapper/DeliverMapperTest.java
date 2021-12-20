package com.inbobwetrust.model.mapper;

import com.inbobwetrust.model.dto.DeliveryCreateDto;
import com.inbobwetrust.model.dto.DeliverySetRiderDto;
import com.inbobwetrust.model.entity.Delivery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@ActiveProfiles("test")
@SpringBootTest
public class DeliverMapperTest {
    @Autowired DeliveryMapper deliveryMapper;

    @Test
    @DisplayName("[DeliveryMapper.fromCreateDtoToEntity] 성공")
    void fromCreateDtoToEntityTest() {
        DeliveryCreateDto expected =
                DeliveryCreateDto.builder()
                        .orderId(1L)
                        .riderId(2L)
                        .agencyId(3L)
                        .pickupTime(LocalDateTime.now().plusMinutes(30))
                        .createdAt(LocalDateTime.now())
                        .build();

        Delivery actual = deliveryMapper.fromCreateDtoToEntity(expected);

        Assertions.assertNull(actual.getId());
        Assertions.assertEquals(expected.getOrderId(), actual.getOrderId());
        Assertions.assertEquals(expected.getRiderId(), actual.getRiderId());
        Assertions.assertEquals(expected.getAgencyId(), actual.getAgencyId());
        Assertions.assertNull(actual.getOrderStatus());
        Assertions.assertEquals(expected.getPickupTime(), actual.getPickupTime());
        Assertions.assertNull(actual.getFinishTime());
        Assertions.assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        Assertions.assertNull(actual.getUpdatedAt());
    }

    @Test
    @DisplayName("[DeliveryMapper.fromCreateDtoToEntity] Null 값")
    void fromCreateDtoToEntityTest2() {
        DeliveryCreateDto expected = DeliveryCreateDto.builder().build();

        Delivery actual = deliveryMapper.fromCreateDtoToEntity(expected);

        Assertions.assertNull(actual.getId());
        Assertions.assertNull(actual.getOrderId());
        Assertions.assertNull(actual.getRiderId());
        Assertions.assertNull(actual.getAgencyId());
        Assertions.assertNull(actual.getOrderStatus());
        Assertions.assertNull(actual.getPickupTime());
        Assertions.assertNull(actual.getFinishTime());
        Assertions.assertNull(actual.getCreatedAt());
        Assertions.assertNull(actual.getUpdatedAt());
    }

    @Test
    @DisplayName("[DeliveryMapper.fromSetRiderDtoToEntity] 성공")
    void fromSetRiderDtoToEntityTest() {
        DeliverySetRiderDto expected =
                DeliverySetRiderDto.builder().orderId(1L).riderId(2L).agencyId(3L).build();

        Delivery actual = deliveryMapper.fromSetRiderDtoToEntity(expected);

        Assertions.assertNull(actual.getId());
        Assertions.assertEquals(expected.getOrderId(), actual.getOrderId());
        Assertions.assertEquals(expected.getRiderId(), actual.getRiderId());
        Assertions.assertEquals(expected.getAgencyId(), actual.getAgencyId());
        Assertions.assertNull(actual.getOrderStatus());
        Assertions.assertNull(actual.getPickupTime());
        Assertions.assertNull(actual.getFinishTime());
        Assertions.assertNull(actual.getCreatedAt());
        Assertions.assertNull(actual.getUpdatedAt());
    }

    @Test
    @DisplayName("[DeliveryMapper.fromSetRiderDtoToEntity] Null 값")
    void fromSetRiderDtoToEntityTest2() {
        DeliverySetRiderDto expected = DeliverySetRiderDto.builder().build();

        Delivery actual = deliveryMapper.fromSetRiderDtoToEntity(expected);

        Assertions.assertNotNull(actual);
        Assertions.assertNull(actual.getId());
        Assertions.assertNull(actual.getOrderId());
        Assertions.assertNull(actual.getRiderId());
        Assertions.assertNull(actual.getAgencyId());
        Assertions.assertNull(actual.getOrderStatus());
        Assertions.assertNull(actual.getPickupTime());
        Assertions.assertNull(actual.getFinishTime());
        Assertions.assertNull(actual.getCreatedAt());
        Assertions.assertNull(actual.getUpdatedAt());
    }
}
