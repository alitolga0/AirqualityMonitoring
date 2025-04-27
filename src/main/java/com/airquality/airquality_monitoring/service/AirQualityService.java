package com.airquality.airquality_monitoring.service;

import com.airquality.airquality_monitoring.dto.AirQualityRequest;
import com.airquality.airquality_monitoring.dto.AirQualityResponse;
import com.airquality.airquality_monitoring.model.AirQualityRecord;
import com.airquality.airquality_monitoring.model.Anomaly;
import com.airquality.airquality_monitoring.repository.AirQualityRepository;
import com.airquality.airquality_monitoring.repository.AnomalyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirQualityService {

    private final AirQualityRepository airQualityRepository;
    private final AnomalyRepository anomalyRepository;
    private final AnomalyDetectionService anomalyDetectionService;

    // Request -> Entity mapleyip kaydediyoruz
    public AirQualityRecord saveAirQualityData(AirQualityRequest request) {
        if (airQualityRepository.existsByMessageId(request.getMessageId())) {
            System.out.println("Duplicate message detected, skipping: " + request.getMessageId());
            return null; // Zaten işlenmiş
        }

        AirQualityRecord record = new AirQualityRecord();
        record.setLatitude(request.getLatitude());
        record.setLongitude(request.getLongitude());
        record.setPm25(request.getPm25());
        record.setPm10(request.getPm10());
        record.setNo2(request.getNo2());
        record.setSo2(request.getSo2());
        record.setO3(request.getO3());
        record.setRecordedAt(LocalDateTime.now());
        record.setMessageId(request.getMessageId());
        AirQualityRecord savedRecord = airQualityRepository.save(record);

        // Anomali kontrolü
        anomalyDetectionService.detectAnomalies(savedRecord);

        return savedRecord;
    }

    // Belirli konum için verileri getir
    public List<AirQualityResponse> getAirQualityDataByLocation(double latitude, double longitude) {
        List<AirQualityRecord> records = airQualityRepository.findByLatitudeAndLongitude(latitude, longitude);
        return records.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    // Anomali listesi (isteğe bağlı tarih aralığı)
    public List<Anomaly> getAnomalies(String startTime, String endTime) {
        if (startTime != null && endTime != null) {
            LocalDateTime start = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return anomalyRepository.findByDetectedAtBetween(start, end);
        } else {
            return anomalyRepository.findAll();
        }
    }

    // Entity → Response DTO mapping
    private AirQualityResponse convertToResponse(AirQualityRecord record) {
        AirQualityResponse response = new AirQualityResponse();
        response.setLatitude(record.getLatitude());
        response.setLongitude(record.getLongitude());
        response.setPm25(record.getPm25());
        response.setPm10(record.getPm10());
        response.setNo2(record.getNo2());
        response.setSo2(record.getSo2());
        response.setO3(record.getO3());
        response.setRecordedAt(record.getRecordedAt());
        return response;
    }
}
