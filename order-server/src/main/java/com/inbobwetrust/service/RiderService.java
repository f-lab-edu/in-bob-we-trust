package com.inbobwetrust.service;

import com.inbobwetrust.model.entity.Rider;
import com.inbobwetrust.repository.RiderLocationRepository;
import com.inbobwetrust.repository.RiderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final RiderLocationRepository riderLocationRepository;

    public Rider updateLocation(Rider rider) {
        riderLocationRepository.putIfAbsentLocation(null);
        return findByRiderId(rider.getId());
    }

    private Rider findByRiderId(Long riderId) {
        return riderRepository
                .findByRiderId(riderId)
                .orElseThrow(() -> new RuntimeException("Cannot find Rider"));
    }
}
