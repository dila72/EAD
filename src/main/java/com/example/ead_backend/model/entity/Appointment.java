package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import com.example.ead_backend.model.enums.AppointmentStatus;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "Appointments")
@Data
public class Appointment {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(255)")
    private String appointmentId;

    @Column(nullable = false)
    private String service;

    @Column(nullable = true)
    private String customerId;

    @Column(nullable = true)
    private String vehicleId;

    @Column(nullable = false)
    private String vehicleNo;

    @Column(nullable = false)
    private LocalDate date; // appointment date

    // Start time (maps to DB column start_time)
    @Column(name = "start_time", nullable = false)
    private String startTime;

    // Legacy column 'time' kept by existing DB; we persist a derived value to satisfy NOT NULL
    @Column(name = "time", nullable = false)
    private String time;

    // Some existing databases enforce a NOT NULL on end_time
    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;
}
