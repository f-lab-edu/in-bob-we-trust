package com.inbobwetrust.model.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Rider {
    private String id;
    private Long agencyId;
    private GeoLocation geoLocation;
}
