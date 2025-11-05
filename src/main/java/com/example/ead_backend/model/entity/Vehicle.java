package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
@Data // Lombok: generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: generates default constructor
@AllArgsConstructor // Lombok: generates constructor with all fields
@Builder // Lombok: allows builder pattern for object creation
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String color;

    private String vin;

    private LocalDate registrationDate;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private int year;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String imagePublicId;
}
