package com.example.ead_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Response DTO for progress update information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {

    private Long id;
    private String appointmentId;
    private String stage;
    private Integer percentage;
    private String remarks;
    private Long updatedBy;
    private Timestamp updatedAt;
}
