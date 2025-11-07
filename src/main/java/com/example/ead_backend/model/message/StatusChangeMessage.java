package com.example.ead_backend.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * WebSocket message for status changes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeMessage {

    private Long appointmentId;
    private String oldStatus;
    private String newStatus;
    private String changedBy;
    private Timestamp timestamp;
}
