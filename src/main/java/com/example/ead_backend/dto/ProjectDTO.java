package com.example.ead_backend.dto;

import java.time.LocalDate;
import com.example.ead_backend.model.enums.ProjectStatus;
import lombok.Data;

@Data
public class ProjectDTO {
    private String projectId;
    private String name;
    private String description;
    private String customerId;
    private String customerName;  // Added for displaying customer name
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private EmployeeDTO employee;
    private Integer progressPercentage;  // Progress percentage for the project
}
