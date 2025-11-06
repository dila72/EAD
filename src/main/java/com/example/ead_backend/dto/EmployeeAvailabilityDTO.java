package com.example.ead_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAvailabilityDTO {
    private Long employeeId;
    private String employeeName;
    private String email;
    private String role;
    private Integer currentAppointmentCount;
    private boolean available;
}
