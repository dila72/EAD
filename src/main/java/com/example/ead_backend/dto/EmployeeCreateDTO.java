package com.example.ead_backend.dto;

import java.time.LocalDate;

public class EmployeeCreateDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private LocalDate joinedDate;

    public EmployeeCreateDTO() {}

    public EmployeeCreateDTO(Long id, String firstName, String lastName, String email, String role, LocalDate joinedDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.joinedDate = joinedDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }
}
