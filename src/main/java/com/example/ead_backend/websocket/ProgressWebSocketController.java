package com.example.ead_backend.websocket;

import com.example.ead_backend.model.message.NotificationMessage;
import com.example.ead_backend.model.message.ProgressUpdateMessage;
import com.example.ead_backend.model.message.StatusChangeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for handling STOMP messages related to progress updates.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProgressWebSocketController {

    /**
     * Handle progress update messages from clients.
     *
     * @param appointmentId the appointment ID
     * @param message       the progress update message
     * @return the message to broadcast to subscribers
     */
    @MessageMapping("/progress/{appointmentId}")
    @SendTo("/topic/progress.{appointmentId}")
    public ProgressUpdateMessage handleProgressUpdate(
            @DestinationVariable Long appointmentId,
            ProgressUpdateMessage message) {
        
        log.info("Received progress update message for appointment {}", appointmentId);
        return message;
    }

    /**
     * Handle status change messages from clients.
     *
     * @param appointmentId the appointment ID
     * @param message       the status change message
     * @return the message to broadcast to subscribers
     */
    @MessageMapping("/status/{appointmentId}")
    @SendTo("/topic/status.{appointmentId}")
    public StatusChangeMessage handleStatusChange(
            @DestinationVariable Long appointmentId,
            StatusChangeMessage message) {
        
        log.info("Received status change message for appointment {}", appointmentId);
        return message;
    }

    /**
     * Handle notification messages for specific users.
     *
     * @param userId  the user ID
     * @param message the notification message
     * @return the message to broadcast to the user
     */
    @MessageMapping("/notifications/{userId}")
    @SendTo("/topic/notifications.{userId}")
    public NotificationMessage handleNotification(
            @DestinationVariable Long userId,
            NotificationMessage message) {
        
        log.info("Received notification message for user {}", userId);
        return message;
    }
}
