package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Employees")
@Data
public class Employee {
    @Id
    private String employeeId;
}
