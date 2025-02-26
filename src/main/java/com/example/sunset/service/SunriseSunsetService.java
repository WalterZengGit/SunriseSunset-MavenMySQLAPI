package com.example.sunset.service;

import com.example.sunset.dto.SunriseSunsetRequest;
import com.example.sunset.dto.SunriseSunsetResponse;
import com.example.sunset.dto.SunriseSunsetResponse.Results;
import com.example.sunset.entity.SunriseSunset;
import com.example.sunset.repository.SunriseSunsetRepository;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.OutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class SunriseSunsetService {

    private final SunriseSunsetRepository repository;
    private final RestTemplate restTemplate;

    @PostConstruct
    public void clearDatabase() {
        repository.deleteAll();
    }

    public SunriseSunsetService(SunriseSunsetRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public SunriseSunsetResponse getSunriseSunsetData(SunriseSunsetRequest request) {
        SunriseSunsetResponse response = new SunriseSunsetResponse();
        DateTimeFormatter apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(request.getStartDate().replace("/", "-"), apiFormatter);

        for (int i = 0; i < request.getDates(); i++) {
            LocalDate date = startDate.plusDays(i);
            String dateStr = date.format(apiFormatter);
            Optional<SunriseSunset> existingData = repository.findByDateAndLatitudeAndLongitude(dateStr, request.getLat(), request.getLng());

            if (existingData.isPresent()) {
                updateExistingData(existingData.get());
                continue;
            }

            String apiUrl = UriComponentsBuilder.fromHttpUrl("https://api.sunrise-sunset.org/json")
                    .queryParam("lat", request.getLat())
                    .queryParam("lng", request.getLng())
                    .queryParam("date", dateStr)
                    .toUriString();

            try {
                SunriseSunsetResponse apiResponse = restTemplate.getForObject(apiUrl, SunriseSunsetResponse.class);
                if (apiResponse != null && "OK".equals(apiResponse.getStatus()) && apiResponse.getResults() != null) {
                    Results result = apiResponse.getResults();
                    long sunriseTimestamp = convertToTimestamp(result.getSunrise());
                    long sunsetTimestamp = convertToTimestamp(result.getSunset());

                    SunriseSunset entity = new SunriseSunset();
                    entity.setDate(dateStr);
                    entity.setLatitude(request.getLat());
                    entity.setLongitude(request.getLng());
                    entity.setSunrise(sunriseTimestamp);
                    entity.setSunset(sunsetTimestamp);
                    entity.setUpdatedAt(LocalDateTime.now());
                    repository.save(entity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    private void updateExistingData(SunriseSunset existingData) {
        existingData.setUpdatedAt(LocalDateTime.now());
        repository.save(existingData);
    }

    private long convertToTimestamp(String timeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH);
            if (timeStr.length() == 10) {
                timeStr = "0" + timeStr;
            }
            LocalTime localTime = LocalTime.parse(timeStr.trim(), formatter);
            LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(ZoneId.of("UTC")), localTime);
            return dateTime.atZone(ZoneId.of("Asia/Taipei")).toInstant().toEpochMilli();
        } catch (Exception e) {
            return 0;
        }
    }

    public void exportToExcel(SunriseSunsetRequest request, OutputStream outputStream) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalDate startDate = LocalDate.parse(request.getStartDate().replace("/", "-"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = startDate.plusDays(request.getDates() - 1);

        List<SunriseSunset> records = repository.findByDateRangeAndLocation(
                startDate.toString(), endDate.toString(), request.getLat(), request.getLng()
        );

        if (records.isEmpty()) {
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sunrise Sunset Data");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"日期(yyyy/MM/dd)", "sunrise(HH:mm:ss)", "sunset(HH:mm:ss)"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            int rowIdx = 1;
            for (SunriseSunset record : records) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(LocalDate.parse(record.getDate()).format(dateFormatter));
                Instant sunriseInstant = Instant.ofEpochMilli(record.getSunrise());
                LocalTime sunriseTime = sunriseInstant.atZone(ZoneId.of("Asia/Taipei")).toLocalTime();
                row.createCell(1).setCellValue(sunriseTime.format(timeFormatter));
                Instant sunsetInstant = Instant.ofEpochMilli(record.getSunset());
                LocalTime sunsetTime = sunsetInstant.atZone(ZoneId.of("Asia/Taipei")).toLocalTime();
                row.createCell(2).setCellValue(sunsetTime.format(timeFormatter));
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}