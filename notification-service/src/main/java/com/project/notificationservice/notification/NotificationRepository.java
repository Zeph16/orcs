package com.project.notificationservice.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTargetInAndStatus(List<Notification.Target> targets, Notification.Status status);
    List<Notification> findByUserIdAndStatus(Long userId, Notification.Status status);
}
