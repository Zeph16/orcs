package com.project.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addSession(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public void sendNotificationToUser(String userId, Object messageObject) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(messageObject);
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendNotificationToAll(Object messageObject) {
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    String message = objectMapper.writeValueAsString(messageObject);
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
