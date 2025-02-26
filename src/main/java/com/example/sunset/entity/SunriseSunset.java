package com.example.sunset.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Entity
@Table(name = "sunrise_sunset")
@Data
public class SunriseSunset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private Double latitude;
    private Double longitude;
    private Long sunrise;
    private Long sunset;
    private LocalDateTime updatedAt;

    @PreUpdate
    @PrePersist
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public LocalTime getSunriseTime() {
        return Instant.ofEpochMilli(sunrise).atZone(ZoneId.of("Asia/Taipei")).toLocalTime();
    }

    public LocalTime getSunsetTime() {
        return Instant.ofEpochMilli(sunset).atZone(ZoneId.of("Asia/Taipei")).toLocalTime();
    }
}
