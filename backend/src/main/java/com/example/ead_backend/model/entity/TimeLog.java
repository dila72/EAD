package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDate;

@Entity
@Table(name = "TimeLogs")
@Data
public class TimeLog {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(255)")
    private String timeLogId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private String startTime; // HH:mm

    @Column(name = "end_time", nullable = false)
    private String endTime; // HH:mm

    @Column(nullable = false)
    private String type; // e.g., BLOCKED, MAINTENANCE
}
