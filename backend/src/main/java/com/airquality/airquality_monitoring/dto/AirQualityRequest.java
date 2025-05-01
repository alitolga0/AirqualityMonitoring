package com.airquality.airquality_monitoring.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AirQualityRequest {
    private UUID messageId;
    private double latitude;
    private double longitude;

    private double pm25;
    private double pm10;
    private double no2;
    private double so2;
    private double o3;
}