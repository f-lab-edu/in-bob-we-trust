package com.inbobwetrust.model.mapper;

import com.inbobwetrust.model.dto.DeliveryCreateDto;
import com.inbobwetrust.model.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring"
        )
public interface DeliveryMapper {
    Delivery fromCreateDtoToEntity(DeliveryCreateDto deliveryCreateDto);
}
