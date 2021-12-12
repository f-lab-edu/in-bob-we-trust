package com.inbobwetrust.service;

import com.inbobwetrust.model.vo.Rider;
import com.inbobwetrust.repository.RiderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;

    public Rider updateLocation(Rider rider) {
        validateRider(rider);
        riderRepository.update(rider);
        return findByRiderId(rider.getRiderId());
    }

    private void validateRider(Rider rider) {
        validateStringOrThrow(rider.getRiderId(), "RiderId cannot be empty");
        validateStringOrThrow(rider.getLocation(), "RiderLocation cannot be empty");
        validateStringOrThrow(rider.getOrderId(), "OrderId cannot be empty");
    }

    private void validateStringOrThrow(String str, String msg) {
        if (str == null || str.isBlank() || str.isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
    }

    private Rider findByRiderId(String riderId) {
        Optional<Rider> updatedRider = riderRepository.findByRiderId(riderId);
        if (updatedRider.isEmpty()) {
            throw new RuntimeException("Cannot find Rider");
        }
        return updatedRider.get();
    }
}
