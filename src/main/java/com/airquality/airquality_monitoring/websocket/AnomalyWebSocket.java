package com.airquality.airquality_monitoring.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class AnomalyWebSocket extends TextWebSocketHandler {

    private static WebSocketSession session;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        AnomalyWebSocket.session = session;
    }

    public static void sendAlert(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
