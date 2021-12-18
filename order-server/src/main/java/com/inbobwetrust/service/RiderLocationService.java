package com.inbobwetrust.service;

import com.inbobwetrust.exceptions.NoAffectedRowsSqlException;
import com.inbobwetrust.model.entity.RiderLocation;
import com.inbobwetrust.repository.RiderLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiderLocationService {
    private final RiderLocationRepository riderLocationRepository;

    public RiderLocation updateLocation(RiderLocation riderLocation) {
        int affectedRows = riderLocationRepository.putIfAbsentLocation(riderLocation);
        if (affectedRows > 0) {
            return riderLocation;
        }
        throw new NoAffectedRowsSqlException();
    }
}
