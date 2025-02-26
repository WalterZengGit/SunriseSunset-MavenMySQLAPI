package com.example.sunset.dto;

import lombok.Data;
import java.util.List;

@Data
public class SunriseSunsetResponse {
    private Results results;
    private String status;
    private List<SunriseSunsetData> data;

    @Data
    public static class Results {
        private String sunrise;
        private String sunset;
    }

    @Data
    public static class SunriseSunsetData {
        private String date;
        private long sunrise;
        private long sunset;
    }
}
