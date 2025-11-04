package com.example.ead_backend.model.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Employees")
@Data
public class Employee {
    @Id
    private String employeeId;
    private String name;
    private String email;
    private String password;
    private String specialization;
    private LocalDate joinedDate;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects;
}
