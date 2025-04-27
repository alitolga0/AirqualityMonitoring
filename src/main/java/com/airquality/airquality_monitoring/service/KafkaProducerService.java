
package com.airquality.airquality_monitoring.service;

import com.airquality.airquality_monitoring.dto.AirQualityRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    @Value(value = "${kafka-topic-name}")
    private String topicName;

    @Autowired
    private final KafkaTemplate<String, AirQualityRequest> kafkaTemplate;

    public void sendAirQualityData(AirQualityRequest data) {
        data.setMessageId(UUID.randomUUID());
        CompletableFuture<SendResult<String, AirQualityRequest>> future = kafkaTemplate.send(topicName,data);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + data +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" +
                        data + "] due to : " + ex.getMessage());
            }
        });
    }
}


