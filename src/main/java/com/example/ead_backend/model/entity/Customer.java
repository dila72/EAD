package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Customers")
@Data
public class Customer {
    @Id
    private String customerId;
}
