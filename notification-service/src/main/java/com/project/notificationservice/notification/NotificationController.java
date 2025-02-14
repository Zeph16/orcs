package com.project.notificationservice.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(@RequestBody NotificationCreateRequest request) {
        Notification notification = notificationService.createNotification(request.getEventType(), request.getEventData(), request.getMessage(), null, Notification.Target.ALL);
        return ResponseEntity.ok(notificationService.mapToDTO(notification));
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> unreadNotifications = notificationService.getNotificationsByUser(userId, List.of(Notification.Target.ALL, Notification.Target.STUDENT), Notification.Status.UNREAD);
        return ResponseEntity.ok(unreadNotifications.stream().map(notificationService::mapToDTO).toList());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications.stream().map(notificationService::mapToDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notificationService.mapToDTO(notification));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationCreateRequest {
        private String eventType;
        private String eventData;
        private String message;
    }
}
