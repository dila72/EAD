package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

/**
 * Entity representing a progress update for an appointment.
 * Tracks the stage, percentage completion, and remarks for service progress.
 */
@Entity
@Table(name = "progress_updates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false)
    private String appointmentId;

    @Column(name = "stage", nullable = false, length = 100)
    private String stage;

    @Column(name = "percentage", nullable = false)
    private Integer percentage;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
