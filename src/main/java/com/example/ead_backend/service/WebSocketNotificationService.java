package com.example.ead_backend.service;

import com.example.ead_backend.dto.ProgressResponse;
import com.example.ead_backend.model.entity.Notification;
import com.example.ead_backend.model.message.NotificationMessage;
import com.example.ead_backend.model.message.ProgressUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Service for broadcasting notifications via WebSocket.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast a progress update to all subscribers of a specific appointment.
     *
     * @param appointmentId the appointment ID
     * @param response      the progress response
     */
    public void broadcastProgressUpdate(Long appointmentId, ProgressResponse response) {
        log.info("Broadcasting progress update for appointment {}", appointmentId);

        ProgressUpdateMessage message = ProgressUpdateMessage.builder()
                .appointmentId(appointmentId)
                .stage(response.getStage())
                .percentage(response.getPercentage())
                .remarks(response.getRemarks())
                .updatedBy(response.getUpdatedBy())
                .timestamp(Timestamp.from(Instant.now()))
                .build();

        String destination = "/topic/progress." + appointmentId;
        messagingTemplate.convertAndSend(destination, message);

        log.debug("Progress update broadcasted to {}", destination);
    }

    /**
     * Send a notification to a specific user.
     *
     * @param userId       the user ID
     * @param notification the notification entity
     */
    public void notifyUser(Long userId, Notification notification) {
        log.info("Sending notification to user {}", userId);

        NotificationMessage message = NotificationMessage.builder()
                .userId(userId)
                .type(notification.getType())
                .message(notification.getMessage())
                .timestamp(notification.getCreatedAt())
                .build();

        String destination = "/topic/notifications." + userId;
        messagingTemplate.convertAndSend(destination, message);

        log.debug("Notification sent to {}", destination);
    }
}
