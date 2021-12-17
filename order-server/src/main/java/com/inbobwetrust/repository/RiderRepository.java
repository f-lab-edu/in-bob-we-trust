package com.inbobwetrust.repository;

import com.inbobwetrust.model.entity.Rider;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RiderRepository {
    boolean save(Rider rider);

    boolean update(Rider rider);

    Optional<Rider> findByRiderId(Long riderId);

    List<Rider> findAll();
}
