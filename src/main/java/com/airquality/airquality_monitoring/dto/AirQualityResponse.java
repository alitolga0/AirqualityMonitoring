package com.airquality.airquality_monitoring.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AirQualityResponse {
    private double latitude;
    private double longitude;

    private double pm25;
    private double pm10;
    private double no2;
    private double so2;
    private double o3;

    private LocalDateTime recordedAt;
}
