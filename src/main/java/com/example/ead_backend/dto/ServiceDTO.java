package com.example.ead_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Service entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Integer estimatedDurationMinutes;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
