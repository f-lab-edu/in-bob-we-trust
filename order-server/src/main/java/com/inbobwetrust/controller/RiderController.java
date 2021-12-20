package com.inbobwetrust.controller;

import com.inbobwetrust.config.swaggerdoc.RiderControllerSwaggerDoc;
import com.inbobwetrust.model.dto.RiderLocationDto;
import com.inbobwetrust.model.mapper.RiderMapper;
import com.inbobwetrust.service.RiderLocationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("rider")
@RequiredArgsConstructor
public class RiderController implements RiderControllerSwaggerDoc {
    private final RiderLocationService riderService;
    private final RiderMapper riderMapper;

    @PatchMapping("location")
    public RiderLocationDto setRiderLocation(
            @RequestBody @Valid RiderLocationDto riderLocationDto) {
        riderMapper.toLocationEntity(riderLocationDto);
        return riderLocationDto;
    }
}
