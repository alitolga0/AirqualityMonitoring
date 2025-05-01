package com.airquality.airquality_monitoring.controller;

import com.airquality.airquality_monitoring.dto.AirQualityRequest;
import com.airquality.airquality_monitoring.dto.AirQualityResponse;
import com.airquality.airquality_monitoring.model.Anomaly;
import com.airquality.airquality_monitoring.model.AirQualityRecord;
import com.airquality.airquality_monitoring.service.AirQualityService;
import com.airquality.airquality_monitoring.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/airquality")
public class AirQualityController {

    private final AirQualityService airQualityService;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public  AirQualityController(AirQualityService airQualityService, KafkaProducerService kafkaProducerService){
        this.airQualityService = airQualityService;
        this.kafkaProducerService = kafkaProducerService;
    }
    // Veri kaydetme — manuel ve otomatik script'ler için
    @PostMapping
    public ResponseEntity<AirQualityRecord> saveAirQuality(@RequestBody AirQualityRequest request) {
        request.setMessageId(UUID.randomUUID());
        AirQualityRecord savedRecord = airQualityService.saveAirQualityData(request);
        return ResponseEntity.ok(savedRecord);
    }

    @PostMapping("/autoSaveAirQuality")
    public ResponseEntity autoSaveAirQuality(@RequestBody AirQualityRequest request) {
        kafkaProducerService.sendAirQualityData(request);
        return ResponseEntity.ok(true);
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

    @GetMapping("/findNearbyRecords")
    public ResponseEntity<List<AirQualityResponse>> findNearbyRecords(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        List<AirQualityResponse> data = airQualityService.findNearbyRecords(latitude, longitude);
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
