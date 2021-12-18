package com.inbobwetrust.repository;

import com.inbobwetrust.model.entity.Rider;

import java.util.List;
import java.util.Optional;

public interface RiderRepository {
    int save(Rider rider);

    Optional<Rider> findByRiderId(Long riderId);

    List<Rider> findAll();

    int deleteAll();
}
