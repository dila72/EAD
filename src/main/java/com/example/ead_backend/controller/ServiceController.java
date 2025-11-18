package com.example.ead_backend.controller;

import com.example.ead_backend.dto.ServiceDTO;
import com.example.ead_backend.service.ServiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for managing predefined services
 * Organized by role-based routing:
 * - Admin operations: /api/admin/services
 * - Customer operations: /api/customer/services
 */
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;
    private final ObjectMapper objectMapper;

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Create a new service without image (JSON body)
     * Admin only
     */
    @PostMapping("/api/admin/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceDTO> createService(@RequestBody ServiceDTO serviceDTO) {
        log.info("Admin creating service: {}", serviceDTO.getName());
        ServiceDTO created = serviceService.createService(serviceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Create a new service with image (multipart form data)
     * Admin only
     */
    @PostMapping(value = "/api/admin/services/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createServiceWithImage(
            @RequestPart("service") String serviceDTOJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            log.info("Admin creating service with image");
            ServiceDTO serviceDTO = objectMapper.readValue(serviceDTOJson, ServiceDTO.class);
            
            if (image != null && !image.isEmpty()) {
                ServiceDTO created = serviceService.createServiceWithImage(serviceDTO, image);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
            } else {
                ServiceDTO created = serviceService.createService(serviceDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
            }
        } catch (IOException e) {
            log.error("Failed to create service with image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create service: " + e.getMessage());
        }
    }

    /**
     * Get all services (including inactive)
     * Admin only
     */
    @GetMapping("/api/admin/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        log.info("Admin fetching all services");
        List<ServiceDTO> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    /**
     * Get service by ID (admin view)
     * Admin only
     */
    @GetMapping("/api/admin/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceDTO> getServiceByIdAdmin(@PathVariable Long id) {
        log.info("Admin fetching service with ID: {}", id);
        ServiceDTO service = serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    /**
     * Update service without changing image (JSON body)
     * Admin only
     */
    @PutMapping("/api/admin/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable Long id,
            @RequestBody ServiceDTO serviceDTO) {
        log.info("Admin updating service with ID: {}", id);
        ServiceDTO updated = serviceService.updateService(id, serviceDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Update service with new image (multipart form data)
     * Admin only
     */
    @PutMapping(value = "/api/admin/services/{id}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateServiceWithImage(
            @PathVariable Long id,
            @RequestPart("service") String serviceDTOJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            log.info("Admin updating service with image, ID: {}", id);
            ServiceDTO serviceDTO = objectMapper.readValue(serviceDTOJson, ServiceDTO.class);
            
            if (image != null && !image.isEmpty()) {
                ServiceDTO updated = serviceService.updateServiceWithImage(id, serviceDTO, image);
                return ResponseEntity.ok(updated);
            } else {
                ServiceDTO updated = serviceService.updateService(id, serviceDTO);
                return ResponseEntity.ok(updated);
            }
        } catch (IOException e) {
            log.error("Failed to update service with image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update service: " + e.getMessage());
        }
    }

    /**
     * Delete a service
     * Admin only
     */
    @DeleteMapping("/api/admin/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        log.info("Admin deleting service with ID: {}", id);
        serviceService.deleteService(id);
        return ResponseEntity.ok("Service deleted successfully");
    }

    /**
     * Toggle service active/inactive status
     * Admin only
     */
    @PatchMapping("/api/admin/services/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceDTO> toggleServiceStatus(@PathVariable Long id) {
        log.info("Admin toggling service status for ID: {}", id);
        ServiceDTO updated = serviceService.toggleServiceStatus(id);
        return ResponseEntity.ok(updated);
    }

    // ==================== CUSTOMER ENDPOINTS ====================

    /**
     * Get all active services for customer selection
     * Accessible by customers and unauthenticated users for browsing
     */
    @GetMapping("/api/customer/services")
    public ResponseEntity<List<ServiceDTO>> getActiveServicesForCustomer() {
        log.info("Customer fetching active services");
        List<ServiceDTO> services = serviceService.getActiveServices();
        return ResponseEntity.ok(services);
    }

    /**
     * Get service details by ID (customer view)
     * Accessible by customers and unauthenticated users for browsing
     */
    @GetMapping("/api/customer/services/{id}")
    public ResponseEntity<ServiceDTO> getServiceByIdCustomer(@PathVariable Long id) {
        log.info("Customer fetching service with ID: {}", id);
        ServiceDTO service = serviceService.getServiceById(id);
        
        // Only return if service is active
        if (!service.getActive()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        
        return ResponseEntity.ok(service);
    }
}
