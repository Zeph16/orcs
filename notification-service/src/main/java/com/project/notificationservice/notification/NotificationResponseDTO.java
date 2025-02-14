package com.project.notificationservice.notification;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    private Long id;

    private String type;
    private String data;
    private String message;

    @Enumerated(EnumType.STRING)
    private Notification.Status status;

    private LocalDateTime createdAt;
}
