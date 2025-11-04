package com.example.ead_backend.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeDTO implements Serializable {
    private String employeeId;
    private String name;
    private String email;
    private String password;
    private String specialization;
    private LocalDate joinedDate;
}