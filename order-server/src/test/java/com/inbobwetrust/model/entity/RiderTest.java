package com.inbobwetrust.model.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RiderTest {
    @Test
    @DisplayName("[Rider.hasAllData]")
    void hasAllDataTest() {
        Assertions.assertFalse(new Rider(null, null).hasAllData());
        Assertions.assertFalse(Rider.builder().id(null).agencyId(1L).build().hasAllData());
        Assertions.assertFalse(Rider.builder().id(1L).agencyId(null).build().hasAllData());

        Assertions.assertTrue(Rider.builder().id(1L).agencyId(2L).build().hasAllData());
    }
}
