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
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private LocalDate joinedDate;
}
