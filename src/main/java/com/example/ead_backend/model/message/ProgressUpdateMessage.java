package com.example.ead_backend.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * WebSocket message for progress updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateMessage {

    private String appointmentId;
    private String stage;
    private Integer percentage;
    private String remarks;
    private Long updatedBy;
    private Timestamp timestamp;
}
