package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TimeLog")
@Data
public class TimeLog {
    @Id
    private String timeLogId;
}
