package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Tasks")
@Data
public class Task {
    @Id
    private String taskId;
}
