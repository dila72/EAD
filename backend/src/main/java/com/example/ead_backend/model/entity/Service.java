package com.example.ead_backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a predefined service that customers can select
 * when booking appointments. Managed by administrators.
 */
@Entity
@Table(name = "services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    /**
     * URL of the service image stored in Cloudinary
     */
    @Column(name = "image_url")
    private String imageUrl;
    
    /**
     * Cloudinary public ID for the image (used for deletion/updates)
     */
    @Column(name = "image_public_id")
    private String imagePublicId;
    
    /**
     * Price of the service
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * Estimated duration in minutes
     */
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;
    
    /**
     * Whether this service is currently active and available for booking
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
