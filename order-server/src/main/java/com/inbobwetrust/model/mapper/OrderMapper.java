package com.inbobwetrust.model.mapper;

import com.inbobwetrust.model.dto.OrderDto;
import com.inbobwetrust.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(OrderDto orderDto);
}
