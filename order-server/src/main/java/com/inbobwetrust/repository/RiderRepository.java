package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Rider;

import java.util.List;
import java.util.Optional;

public interface RiderRepository {
    boolean save(Rider rider);

    boolean update(Rider rider);

    Optional<Rider> findByRiderId(String riderId);

    List<Rider> findAll();
}
