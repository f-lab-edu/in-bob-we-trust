package com.inbobwetrust.repository;

import com.inbobwetrust.model.entity.RiderLocation;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RiderLocationRepository {
    int putIfAbsentLocation(RiderLocation location);

    Optional<RiderLocation> findByRiderId(Long riderId);

    int deleteAll();

    List<RiderLocation> findAll();
}
