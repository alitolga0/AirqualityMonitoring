package com.airquality.airquality_monitoring.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Getter
@Setter
@Table(name = "air_quality_record")
public class AirQualityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID messageId;

    private double latitude;
    private double longitude;

    private double pm25;
    private double pm10;
    private double no2;
    private double so2;
    private double o3;

    private LocalDateTime recordedAt;
}