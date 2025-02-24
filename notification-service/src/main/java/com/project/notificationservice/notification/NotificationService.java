package com.project.notificationservice.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(String type, String data, String message, String userId, Notification.Target target) {
        Notification notification = Notification.builder()
                .type(type)
                .data(data)
                .message(message)
                .status(Notification.Status.UNREAD)
                .target(target)
                .userId(userId)
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUser(String userId, List<Notification.Target> targets, Notification.Status status) {
        return Stream.concat(
                notificationRepository.findByTargetInAndStatus(targets, status).stream(),
                notificationRepository.findByUserIdAndStatus(userId, status).stream()
        ).toList();
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }


    public void markAsRead(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(n -> {
            n.setStatus(Notification.Status.READ);
            notificationRepository.save(n);
        });
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElseThrow();
    }

    public NotificationResponseDTO mapToDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .data(notification.getData())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
