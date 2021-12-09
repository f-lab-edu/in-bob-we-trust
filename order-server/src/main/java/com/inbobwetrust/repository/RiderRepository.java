package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Rider;

import java.util.Optional;

public interface RiderRepository {

    boolean update(Rider rider);

    Optional<Rider> findByRiderId(String riderId);
}
