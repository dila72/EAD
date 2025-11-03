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

    private String model;
    private String color;
    private String vin;
    private LocalDate registrationDate;
    private String licensePlate;
    private int year;
}
