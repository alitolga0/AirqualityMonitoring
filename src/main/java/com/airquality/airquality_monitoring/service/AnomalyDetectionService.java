package com.airquality.airquality_monitoring.service;

import com.airquality.airquality_monitoring.model.AirQualityRecord;
import com.airquality.airquality_monitoring.model.Anomaly;
import com.airquality.airquality_monitoring.repository.AnomalyRepository;
import com.airquality.airquality_monitoring.config.ThresholdSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private final AnomalyRepository anomalyRepository;
    private final ThresholdSettings thresholdSettings;

    // Tek tek parametreleri kontrol eder, anomaly kaydeder
    public void detectAnomalies(AirQualityRecord record) {
        checkThreshold("PM2.5", record.getLatitude(), record.getLongitude(), record.getPm25(), thresholdSettings.getPm25());
        checkThreshold("PM10", record.getLatitude(), record.getLongitude(), record.getPm10(), thresholdSettings.getPm10());
        checkThreshold("NO2", record.getLatitude(), record.getLongitude(), record.getNo2(), thresholdSettings.getNo2());
        checkThreshold("SO2", record.getLatitude(), record.getLongitude(), record.getSo2(), thresholdSettings.getSo2());
        checkThreshold("O3", record.getLatitude(), record.getLongitude(), record.getO3(), thresholdSettings.getO3());
    }

    private void checkThreshold(String parameter, double lat, double lon, double value, double limit) {
        if (value > limit) {
            Anomaly anomaly = new Anomaly();
            anomaly.setLatitude(lat);
            anomaly.setLongitude(lon);
            anomaly.setParameter(parameter);
            anomaly.setValue(value);
            anomaly.setType("WHO Threshold");
            anomaly.setDetectedAt(LocalDateTime.now());

            anomalyRepository.save(anomaly);
        }
    }
}
