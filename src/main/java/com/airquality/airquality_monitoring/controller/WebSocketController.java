package com.airquality.airquality_monitoring.controller;

import com.airquality.airquality_monitoring.model.AirQualityRecord;
import com.airquality.airquality_monitoring.model.Anomaly;
import com.airquality.airquality_monitoring.service.AnomalyDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;
@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Servis tarafından çağrılacak
    public void sendAnomaly(Anomaly anomaly) {
        messagingTemplate.convertAndSend("/topic/anomalies", anomaly);
    }
}
