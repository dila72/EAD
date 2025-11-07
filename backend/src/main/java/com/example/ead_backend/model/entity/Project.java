package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import org.hibernate.annotations.UuidGenerator;
import com.example.ead_backend.model.enums.ProjectStatus;

@Entity
@Table(name = "Projects")
@Data
public class Project {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(255)")
    private String projectId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true, length = 2000)
    private String description;

    @Column(nullable = true)
    private String customerId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;
}
