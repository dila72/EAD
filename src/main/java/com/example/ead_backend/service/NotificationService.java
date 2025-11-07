package com.example.ead_backend.service;

import com.example.ead_backend.dto.NotificationDTO;
import com.example.ead_backend.model.enums.NotificationType;

import java.util.List;

/**
 * Service interface for managing notifications.
 */
public interface NotificationService {

    /**
     * Create and save a notification for a user.
     *
     * @param userId  the user ID
     * @param type    the notification type
     * @param message the notification message
     * @return the created notification DTO
     */
    NotificationDTO createNotification(Long userId, NotificationType type, String message);

    /**
     * Get all notifications for a user.
     *
     * @param userId the user ID
     * @return list of notification DTOs
     */
    List<NotificationDTO> getNotificationsForUser(Long userId);

    /**
     * Mark a notification as read.
     *
     * @param notificationId the notification ID
     */
    void markAsRead(Long notificationId);
}
