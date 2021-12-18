package com.inbobwetrust.repository;

import com.inbobwetrust.model.entity.Rider;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RiderRepository {
    int save(Rider rider);

    Optional<Rider> findByRiderId(Long riderId);

    List<Rider> findAll();

    int deleteAll();
}
