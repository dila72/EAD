package com.example.ead_backend.dto;

import java.time.LocalDate;
import com.example.ead_backend.model.enums.AppointmentStatus;
import lombok.Data;

@Data
public class AppointmentDTO {
    private String appointmentId;
    private String service;
    private String customerId;
    private String customerName;  // Added for displaying customer name
    private String vehicleId;
    private String vehicleNo;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private AppointmentStatus status;
    private EmployeeDTO employee;
}
