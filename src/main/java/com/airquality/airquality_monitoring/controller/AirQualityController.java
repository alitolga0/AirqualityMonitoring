package com.airquality.airquality_monitoring.controller;

import com.airquality.airquality_monitoring.dto.AirQualityRequest;
import com.airquality.airquality_monitoring.dto.AirQualityResponse;
import com.airquality.airquality_monitoring.model.Anomaly;
import com.airquality.airquality_monitoring.model.AirQualityRecord;
import com.airquality.airquality_monitoring.service.AirQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airquality")
@RequiredArgsConstructor
public class AirQualityController {

    private final AirQualityService airQualityService;

    // Veri kaydetme — manuel ve otomatik script'ler için
    @PostMapping
    public ResponseEntity<AirQualityRecord> saveAirQuality(@RequestBody AirQualityRequest request) {
        AirQualityRecord savedRecord = airQualityService.saveAirQualityData(request);
        return ResponseEntity.ok(savedRecord);
    }

    // Belirli bir konum için hava kalitesi verilerini getirme
    @GetMapping("/location")
    public ResponseEntity<List<AirQualityResponse>> getAirQualityByLocation(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        List<AirQualityResponse> data = airQualityService.getAirQualityDataByLocation(latitude, longitude);
        return ResponseEntity.ok(data);
    }

    // Anomalileri listeleme (isteğe bağlı zaman aralığı)
    @GetMapping("/anomalies")
    public ResponseEntity<List<Anomaly>> getAnomalies(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        List<Anomaly> anomalies = airQualityService.getAnomalies(startTime, endTime);
        return ResponseEntity.ok(anomalies);
    }

}
