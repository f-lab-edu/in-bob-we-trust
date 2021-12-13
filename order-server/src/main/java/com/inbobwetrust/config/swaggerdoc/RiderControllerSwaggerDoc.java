package com.inbobwetrust.config.swaggerdoc;

import com.inbobwetrust.model.vo.Rider;
import org.springframework.http.ResponseEntity;

public interface RiderControllerSwaggerDoc {
    ResponseEntity<Rider> setRiderLocation(Rider body);
}
