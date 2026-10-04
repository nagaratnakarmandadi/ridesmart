package com.bikerental.service;

import com.bikerental.entity.Notification;
import com.bikerental.entity.User;
import com.bikerental.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void sendUserNotification(User user, String title, String message, String type, String targetUrl) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public void sendAdminBroadcast(String title, String message, String type, String targetUrl) {
        Notification notification = Notification.builder()
                .user(null)
                .title(title)
                .message(message)
                .notificationType(type)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }
}
