package com.airquality.airquality_monitoring.repository;

import com.airquality.airquality_monitoring.model.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {
    List<Anomaly> findByDetectedAtBetween(LocalDateTime start, LocalDateTime end);
}