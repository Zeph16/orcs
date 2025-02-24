package com.project.notificationservice.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(@RequestBody NotificationCreateRequest request) {
        Notification notification = notificationService.createNotification(request.getEventType(), request.getEventData(), request.getMessage(), null, Notification.Target.ALL);
        return ResponseEntity.ok(notificationService.mapToDTO(notification));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByUser(@PathVariable String userId) {
        List<Notification> unreadNotifications = notificationService.getNotificationsByUser(userId, List.of(Notification.Target.ALL, Notification.Target.STUDENT), Notification.Status.UNREAD);
        List<Notification> readNotifications = notificationService.getNotificationsByUser(userId, List.of(Notification.Target.ALL, Notification.Target.STUDENT), Notification.Status.READ);
        List<Notification> allNotifications = Stream.concat(
                unreadNotifications.stream(),
                readNotifications.stream()
        ).toList();
        return ResponseEntity.ok(allNotifications.stream().map(notificationService::mapToDTO).toList());
    }


    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable String userId) {
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
