package com.example.ead_backend.service;

import com.example.ead_backend.dto.ServiceDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for managing predefined services that customers can select for appointments
 */
public interface ServiceService {
    
    /**
     * Create a new service without image
     */
    ServiceDTO createService(ServiceDTO serviceDTO);
    
    /**
     * Create a new service with image
     */
    ServiceDTO createServiceWithImage(ServiceDTO serviceDTO, MultipartFile image) throws IOException;
    
    /**
     * Get service by ID
     */
    ServiceDTO getServiceById(Long id);
    
    /**
     * Get all services (including inactive)
     */
    List<ServiceDTO> getAllServices();
    
    /**
     * Get all active services (for customer selection)
     */
    List<ServiceDTO> getActiveServices();
    
    /**
     * Update service without changing image
     */
    ServiceDTO updateService(Long id, ServiceDTO serviceDTO);
    
    /**
     * Update service with new image
     */
    ServiceDTO updateServiceWithImage(Long id, ServiceDTO serviceDTO, MultipartFile image) throws IOException;
    
    /**
     * Delete a service (and its image from Cloudinary)
     */
    void deleteService(Long id);
    
    /**
     * Toggle service active status
     */
    ServiceDTO toggleServiceStatus(Long id);
}
