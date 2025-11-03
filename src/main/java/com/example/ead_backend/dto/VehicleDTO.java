package com.example.ead_backend.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {
    private Long id;
    private String model;
    private String color;
    private String licensePlate;
    private int year;
    private LocalDate registrationDate;
}
