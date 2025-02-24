package com.project.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.notificationservice.notification.Notification;
import com.project.notificationservice.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.project.notificationservice.notification.Notification.Target.STUDENT;
import static com.project.notificationservice.notification.Notification.Target.USER;

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

    public void handleStudentCreated(String eventType, Map<String, Object> eventData) throws JsonProcessingException {
        System.out.println("Processing student creation: " + eventData);
        String message = "Welcome to the HiLCoE online portal! You'll get the latest updates related to your education at HiLCoE on this page. Feel free to look around and familiarize yourself with the rest of the platform.";
        Notification notification = notificationService.createNotification(eventType, eventData.toString(), message, (String) eventData.get("studentCardId"), USER);
        String jsonNotif = objectMapper.writeValueAsString(notificationService.mapToDTO(notification));

        webSocketService.sendNotificationToUser((String) eventData.get("studentCardId"), jsonNotif);
    }

    public void handleAddRecommendationCreated(String eventType, Map<String, Object> eventData) throws JsonProcessingException {
        System.out.println("Processing add recommendation creation: " + eventData);
        String message = "You have been recommended to add the course \"" + eventData.get("courseTitle") + " (" + eventData.get("courseCode") + ")\" in the term " + eventData.get("termCode") + ".";
        Notification notification = notificationService.createNotification(eventType, eventData.toString(), message, (String) eventData.get("studentCardId"), USER);
        String jsonNotif = objectMapper.writeValueAsString(notificationService.mapToDTO(notification));

        webSocketService.sendNotificationToUser((String) eventData.get("studentCardId"), jsonNotif);
    }

    public void handleEnrollmentRequestApproved(String eventType, Map<String, Object> eventData) throws JsonProcessingException {
        System.out.println("Processing enrollment request approved: " + eventData);
        String message = "Your enrollment request for \"" + eventData.get("courseTitle") + " (" + eventData.get("courseCode") + ")\" has been approved!";
        Notification notification = notificationService.createNotification(eventType, eventData.toString(), message, (String) eventData.get("studentCardId"), USER);
        String jsonNotif = objectMapper.writeValueAsString(notificationService.mapToDTO(notification));

        webSocketService.sendNotificationToUser((String) eventData.get("studentCardId"), jsonNotif);
    }

    public void handleEnrollmentRequestDenied(String eventType, Map<String, Object> eventData) throws JsonProcessingException {
        System.out.println("Processing enrollment request denied: " + eventData);
        String message = "Your enrollment request for \"" + eventData.get("courseTitle") + " (" + eventData.get("courseCode") + ")\" has been denied!";
        Notification notification = notificationService.createNotification(eventType, eventData.toString(), message, (String) eventData.get("studentCardId"), USER);
        String jsonNotif = objectMapper.writeValueAsString(notificationService.mapToDTO(notification));

        webSocketService.sendNotificationToUser((String) eventData.get("studentCardId"), jsonNotif);
    }

    public void handleRecordCreated(String eventType, Map<String, Object> eventData) throws JsonProcessingException {
        System.out.println("Processing record created: " + eventData);
        String message = "Your have received your result for \"" + eventData.get("courseTitle") + " (" + eventData.get("courseCode") + ")\", for the term " + eventData.get("termCode") + ". Visit the Academic Records page to know more.";
        Notification notification = notificationService.createNotification(eventType, eventData.toString(), message, (String) eventData.get("studentCardId"), USER);
        String jsonNotif = objectMapper.writeValueAsString(notificationService.mapToDTO(notification));

        webSocketService.sendNotificationToUser((String) eventData.get("studentCardId"), jsonNotif);
    }
}
