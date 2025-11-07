package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Service entity operations
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    /**
     * Find all active services
     */
    List<Service> findByActiveTrue();
    
    /**
     * Find service by name
     */
    Optional<Service> findByName(String name);
    
    /**
     * Check if a service exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
}
