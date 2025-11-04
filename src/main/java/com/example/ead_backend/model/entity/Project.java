package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Projects")
@Data
public class Project {
    @Id
    private String projectId;
    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = true)
    private Employee employee;
}
