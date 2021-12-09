package com.inbobwetrust.repository;

import com.inbobwetrust.model.vo.Rider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TestRiderRepositoryImpl implements RiderRepository {
    private static final List<Rider> riders = new ArrayList<>();

    @Override
    public boolean save(Rider rider) {
        if (containsRiderId(rider)) return false;
        rider.setLastUpdated(LocalDateTime.now());
        riders.add(rider);
        return true;
    }

    @Override
    public boolean update(Rider rider) {
        if (!containsRiderId(rider)) return false;
        rider.setLastUpdated(LocalDateTime.now());
        int idx = findIdxByRiderIdAndOrderId(rider);
        riders.set(idx, rider);
        return true;
    }

    @Override
    public Optional<Rider> findByRiderId(String riderId) {
        return riders.stream()
                .filter(existing -> existing.getRiderId().equals(riderId))
                .findFirst();
    }

    @Override
    public List<Rider> findAll() {
        return Collections.unmodifiableList(riders);
    }

    private boolean containsRiderId(Rider rider) {
        return riders.stream()
                .anyMatch(existing -> existing.getRiderId().equals(rider.getRiderId()));
    }

    private int findIdxByRiderIdAndOrderId(Rider rider) {
        for (int i = 0; i < riders.size(); i++) {
            boolean sameRiderId = riders.get(i).getRiderId().equals(rider.getRiderId());
            boolean sameOrderId = riders.get(i).getOrderId().equals(rider.getOrderId());
            if (sameRiderId && sameOrderId) return i;
        }
        throw new RuntimeException("Unexpected Output : missing rider");
    }

    public void clear() {
        while (!riders.isEmpty()) {
            riders.remove(0);
        }
    }
}
