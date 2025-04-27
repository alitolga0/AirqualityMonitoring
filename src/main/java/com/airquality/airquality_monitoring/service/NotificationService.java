
package com.airquality.airquality_monitoring.service;

import com.airquality.airquality_monitoring.model.Anomaly;
import com.airquality.airquality_monitoring.websocket.AnomalyWebSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    public void sendAnomalyNotification(Anomaly anomaly) {
        try {
            String message = "Anomaly Detected: " + anomaly.getParameter() + " - " + anomaly.getValue();
            AnomalyWebSocket.sendAlert(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


