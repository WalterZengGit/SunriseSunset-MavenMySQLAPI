package com.example.sunset.controller;

import com.example.sunset.dto.SunriseSunsetRequest;
import com.example.sunset.service.SunriseSunsetService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/your_export_api")
public class ExportController {

    private final SunriseSunsetService service;

    public ExportController(SunriseSunsetService service) {
        this.service = service;
    }

    @PostMapping
    public void exportToExcel(@RequestBody SunriseSunsetRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=sunrise_sunset_data.xlsx");
        service.exportToExcel(request, response.getOutputStream());
    }
}
