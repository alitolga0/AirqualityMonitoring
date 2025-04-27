package com.airquality.airquality_monitoring.service;

import com.airquality.airquality_monitoring.dto.AirQualityRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final AirQualityService airQualityService;
    private final AnomalyDetectionService anomalyDetectionService;

    @KafkaListener(topics = "air-quality-events", groupId = "airquality-group")
    public void consume(AirQualityRequest data) {
        System.out.println("Received Message in group foo: " + data);
        var record = airQualityService.saveAirQualityData(data);
    }

    @KafkaListener(topics = "topicName")
    public void listenWithHeaders(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        System.out.println(
                "Received Message: " + message
                        + "from partition: " + partition);
    }
}

