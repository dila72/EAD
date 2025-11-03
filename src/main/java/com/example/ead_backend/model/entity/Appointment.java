package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Appointments")
@Data
public class Appointment {
    @Id
    private String appointmentId;
}
