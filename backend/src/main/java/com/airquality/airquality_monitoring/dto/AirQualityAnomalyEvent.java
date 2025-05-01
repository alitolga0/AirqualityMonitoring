package com.airquality.airquality_monitoring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AirQualityAnomalyEvent {
    private double latitude;
    private double longitude;

    private String parameter;  // Örn: "PM2.5"
    private double value;
    private String type;       // Örn: "WHO Threshold"

    private LocalDateTime detectedAt;
}