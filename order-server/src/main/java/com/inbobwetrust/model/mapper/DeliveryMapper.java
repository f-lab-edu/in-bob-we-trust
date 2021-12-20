package com.inbobwetrust.model.mapper;

import com.inbobwetrust.model.dto.DeliveryCreateDto;
import com.inbobwetrust.model.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeliveryMapper {

    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    Delivery fromCreateDtoToEntity(DeliveryCreateDto deliveryCreateDto);
}
