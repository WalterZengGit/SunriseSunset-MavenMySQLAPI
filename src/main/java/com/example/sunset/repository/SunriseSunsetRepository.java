package com.example.sunset.repository;

import com.example.sunset.entity.SunriseSunset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SunriseSunsetRepository extends JpaRepository<SunriseSunset, Long> {
    Optional<SunriseSunset> findByDateAndLatitudeAndLongitude(String date, Double latitude, Double longitude);

    @Query("SELECT s FROM SunriseSunset s WHERE s.date BETWEEN :startDate AND :endDate " +
            "AND s.latitude = :lat AND s.longitude = :lng ORDER BY s.date ASC")
    List<SunriseSunset> findByDateRangeAndLocation(@Param("startDate") String startDate,
                                                   @Param("endDate") String endDate,
                                                   @Param("lat") double lat,
                                                   @Param("lng") double lng);
}
