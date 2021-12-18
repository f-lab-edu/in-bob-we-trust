package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.RiderControllerSwaggerDoc;
import com.inbobwetrust.model.entity.RiderLocation;
import com.inbobwetrust.service.RiderLocationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rider")
@RequiredArgsConstructor
public class RiderController implements RiderControllerSwaggerDoc {
    private final RiderLocationService riderService;

    @PatchMapping("location")
    public RiderLocation setRiderLocation(@RequestBody RiderLocation body) {
        return riderService.updateLocation(body);
    }
}
