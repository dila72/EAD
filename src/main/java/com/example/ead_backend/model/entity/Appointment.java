package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Appointments")
@Data
public class Appointment {
    @Id
    private String appointmentId;
    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = true)
    private Employee employee;
}
