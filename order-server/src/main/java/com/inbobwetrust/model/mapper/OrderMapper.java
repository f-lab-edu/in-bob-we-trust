package com.inbobwetrust.model.mapper;

import com.inbobwetrust.model.dto.OrderDto;
import com.inbobwetrust.model.entity.Order;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    Order toEntity(OrderDto orderDto);
}
