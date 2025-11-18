package com.example.ead_backend.dto;

import com.example.ead_backend.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * DTO for notification information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private Boolean isRead;
    private Timestamp createdAt;
}
