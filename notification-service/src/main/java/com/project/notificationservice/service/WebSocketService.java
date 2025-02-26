package com.project.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.notificationservice.notification.Notification;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.project.notificationservice.notification.Notification;

@Service
public class WebSocketService {

    private final Map<String, WebSocketSession> studentSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> adminSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addStudentSession(String userId, WebSocketSession session) {
        studentSessions.put(userId, session);
    }
    public void addAdminSession(String userId, WebSocketSession session) {
        adminSessions.put(userId, session);
    }

    public void removeStudentSession(String userId) {
        studentSessions.remove(userId);
    }
    public void removeAdminSession(String userId) {
        adminSessions.remove(userId);
    }

    public void sendNotificationToUser(Notification.Target target, String userId, Object messageObject) {
        WebSocketSession session;
        if (target == Notification.Target.STUDENT) {
            session = studentSessions.get(userId);
        } else {
            session = adminSessions.get(userId);
        }
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(messageObject);
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendNotificationToAll(Notification.Target target, Object messageObject) {
        if (target == Notification.Target.STUDENT) {
            studentSessions.values().forEach(session -> {
                if (session.isOpen()) {
                    try {
                        String message = objectMapper.writeValueAsString(messageObject);
                        session.sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            adminSessions.values().forEach(session -> {
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
}
