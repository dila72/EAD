package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Projects")
@Data
public class Project {
    @Id
    private String projectId;
}
