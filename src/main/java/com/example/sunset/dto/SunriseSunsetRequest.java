package com.example.sunset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // ✅ 加入這行
@AllArgsConstructor
public class SunriseSunsetRequest {
    private String startDate;
    private int dates;
    private double lat;
    private double lng;
}
