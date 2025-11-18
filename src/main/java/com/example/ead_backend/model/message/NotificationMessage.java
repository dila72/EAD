package com.example.ead_backend.model.message;

import com.example.ead_backend.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * WebSocket message for general notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private Long userId;
    private NotificationType type;
    private String message;
    private Timestamp timestamp;
}
