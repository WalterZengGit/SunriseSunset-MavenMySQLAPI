package com.example.sunset.controller;

import com.example.sunset.dto.SunriseSunsetRequest;
import com.example.sunset.dto.SunriseSunsetResponse;
import com.example.sunset.service.SunriseSunsetService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/your_api")
public class SunriseSunsetController {

    private final SunriseSunsetService service;

    public SunriseSunsetController(SunriseSunsetService service) {
        this.service = service;
    }

    @PostMapping
    public SunriseSunsetResponse getSunriseSunset(@RequestBody SunriseSunsetRequest request) {
        return service.getSunriseSunsetData(request);
    }
}
