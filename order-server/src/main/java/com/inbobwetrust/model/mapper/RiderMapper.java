package com.inbobwetrust.model.mapper;

import com.inbobwetrust.model.dto.RiderLocationDto;
import com.inbobwetrust.model.entity.RiderLocation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RiderMapper {
    RiderLocation toLocationEntity(RiderLocationDto riderLocationDto);
}
