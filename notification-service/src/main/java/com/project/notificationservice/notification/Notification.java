package com.project.notificationservice.notification;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String data;
    private String message;
    private String userId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Target target;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Status {
        READ, UNREAD
    }

    public enum Target {
        USER, STUDENT, ADMIN, ALL
    }
}
