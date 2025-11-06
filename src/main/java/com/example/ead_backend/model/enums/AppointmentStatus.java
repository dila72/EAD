package com.example.ead_backend.model.enums;

public enum AppointmentStatus {
    PENDING,      // Newly created, awaiting admin assignment
    UPCOMING,     // Assigned to employee
    COMPLETED,
    CANCELLED
}
