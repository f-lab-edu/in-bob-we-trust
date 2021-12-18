package com.inbobwetrust.service;

import com.inbobwetrust.model.entity.Rider;
import com.inbobwetrust.repository.RiderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;

    private Rider findByRiderId(Long riderId) {
        return riderRepository
                .findByRiderId(riderId)
                .orElseThrow(() -> new RuntimeException("Cannot find Rider"));
    }
}
