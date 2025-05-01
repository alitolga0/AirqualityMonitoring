package com.airquality.airquality_monitoring.repository;
import com.airquality.airquality_monitoring.model.AirQualityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AirQualityRepository extends JpaRepository<AirQualityRecord, Long> {

    List<AirQualityRecord> findByLatitudeAndLongitudeAndRecordedAtBetween(double latitude, double longitude, LocalDateTime start, LocalDateTime end);

    List<AirQualityRecord> findByLatitudeAndLongitude(double latitude, double longitude);
    boolean existsByMessageId(UUID messageId);

    @Query("SELECT a FROM AirQualityRecord a WHERE ABS(a.latitude - :latitude) < 0.25 AND ABS(a.longitude - :longitude) < 0.25")
    List<AirQualityRecord> findNearbyRecords(@Param("latitude") double latitude, @Param("longitude") double longitude);

}
