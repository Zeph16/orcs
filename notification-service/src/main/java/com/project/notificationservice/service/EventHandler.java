package com.project.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.notificationservice.notification.Notification;
import com.project.notificationservice.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.project.notificationservice.notification.Notification.Target.STUDENT;

@Service
public class EventHandler {
    private final WebSocketService webSocketService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public EventHandler(WebSocketService webSocketService, NotificationService notificationService, ObjectMapper objectMapper) {
        this.webSocketService = webSocketService;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }


    public void handleTermCreated(String eventType, Map<String, Object> eventData) throws JsonProcessingException {
        System.out.println("Processing term creation: " + eventData);
        String message = "A new term, " + eventData.get("code") +" , has been initiated. Please plan your enrollments accordingly.";
        Notification notification = notificationService.createNotification(eventType, eventData.toString(), message, null, STUDENT);
        String jsonNotif = objectMapper.writeValueAsString(notificationService.mapToDTO(notification));
        webSocketService.sendNotificationToAll(jsonNotif);
    }
}
