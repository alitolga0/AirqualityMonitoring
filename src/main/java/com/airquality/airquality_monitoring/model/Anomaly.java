package com.airquality.airquality_monitoring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@Table(name = "anomaly")
public class Anomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;

    private String parameter;  // "PM2.5", "NO2", vb.
    private double value;
    private String type;       // "WHO Threshold", "Spike", vb.

    private LocalDateTime detectedAt;
}