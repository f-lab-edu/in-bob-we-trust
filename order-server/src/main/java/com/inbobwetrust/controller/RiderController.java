package com.inbobwetrust.controller;

import com.inbobwetrust.model.vo.RiderLocation;
import com.inbobwetrust.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rider")
@RequiredArgsConstructor
public class RiderController {
    private final RiderService riderService;

    @PatchMapping("location")
    public ResponseEntity<RiderLocation> setRiderLocation(@RequestBody RiderLocation body) {
        RiderLocation riderLocation = riderService.updateLocation(body);
        return new ResponseEntity<>(riderLocation, HttpStatus.OK);
    }
}
