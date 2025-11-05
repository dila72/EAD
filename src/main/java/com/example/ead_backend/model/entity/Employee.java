package com.example.ead_backend.model.entity;

import java.time.LocalDate;
import java.util.List;

import com.example.ead_backend.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN or EMPLOYEE

    @Column(nullable = false)
    private LocalDate joinedDate;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects;

    public Employee(User user, Role role, LocalDate joinedDate) {
        this.user = user;
        this.role = role;
        this.joinedDate = joinedDate;
    }
}
