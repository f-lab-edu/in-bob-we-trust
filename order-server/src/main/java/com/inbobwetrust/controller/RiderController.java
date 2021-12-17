package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.RiderControllerSwaggerDoc;
import com.inbobwetrust.model.entity.Rider;
import com.inbobwetrust.service.RiderService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rider")
@RequiredArgsConstructor
public class RiderController implements RiderControllerSwaggerDoc {
    private final RiderService riderService;

    @PatchMapping("location")
    public Rider setRiderLocation(@RequestBody Rider body) {
        return riderService.updateLocation(body);
    }
}
