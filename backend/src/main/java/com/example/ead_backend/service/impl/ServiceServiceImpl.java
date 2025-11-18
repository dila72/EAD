package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.ServiceDTO;
import com.example.ead_backend.mapper.ServiceMapper;
import com.example.ead_backend.model.entity.Service;
import com.example.ead_backend.repository.ServiceRepository;
import com.example.ead_backend.service.LocalFileStorageService;
import com.example.ead_backend.service.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ServiceService for managing predefined services
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final LocalFileStorageService fileStorageService;

    @Override
    @Transactional
    public ServiceDTO createService(ServiceDTO serviceDTO) {
        log.info("Creating service: {}", serviceDTO.getName());
        
        // Check if service with same name already exists
        if (serviceRepository.existsByNameIgnoreCase(serviceDTO.getName())) {
            throw new RuntimeException("Service with name '" + serviceDTO.getName() + "' already exists");
        }
        
        Service service = serviceMapper.toEntity(serviceDTO);
        Service saved = serviceRepository.save(service);
        
        log.info("Service created successfully with ID: {}", saved.getId());
        return serviceMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ServiceDTO createServiceWithImage(ServiceDTO serviceDTO, MultipartFile image) throws IOException {
        log.info("Creating service with image: {}", serviceDTO.getName());
        
        // Check if service with same name already exists
        if (serviceRepository.existsByNameIgnoreCase(serviceDTO.getName())) {
            throw new RuntimeException("Service with name '" + serviceDTO.getName() + "' already exists");
        }
        
        // Upload image to local storage
        Map<String, Object> uploadResult = fileStorageService.uploadImage(image, "services");
        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");
        
        // Create service entity
        Service service = serviceMapper.toEntity(serviceDTO);
        service.setImageUrl(imageUrl);
        service.setImagePublicId(publicId);
        
        Service saved = serviceRepository.save(service);
        
        log.info("Service with image created successfully with ID: {}", saved.getId());
        return serviceMapper.toDTO(saved);
    }

    @Override
    public ServiceDTO getServiceById(Long id) {
        log.info("Fetching service with ID: {}", id);
        
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
        
        return serviceMapper.toDTO(service);
    }

    @Override
    public List<ServiceDTO> getAllServices() {
        log.info("Fetching all services");
        
        return serviceRepository.findAll()
                .stream()
                .map(serviceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDTO> getActiveServices() {
        log.info("Fetching active services");
        
        return serviceRepository.findByActiveTrue()
                .stream()
                .map(serviceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceDTO updateService(Long id, ServiceDTO serviceDTO) {
        log.info("Updating service with ID: {}", id);
        
        Service existing = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
        
        // Check if name is being changed to an existing name
        if (!existing.getName().equalsIgnoreCase(serviceDTO.getName()) &&
            serviceRepository.existsByNameIgnoreCase(serviceDTO.getName())) {
            throw new RuntimeException("Service with name '" + serviceDTO.getName() + "' already exists");
        }
        
        // Update fields
        existing.setName(serviceDTO.getName());
        existing.setDescription(serviceDTO.getDescription());
        existing.setPrice(serviceDTO.getPrice());
        existing.setEstimatedDurationMinutes(serviceDTO.getEstimatedDurationMinutes());
        if (serviceDTO.getActive() != null) {
            existing.setActive(serviceDTO.getActive());
        }
        
        Service updated = serviceRepository.save(existing);
        
        log.info("Service updated successfully: {}", updated.getId());
        return serviceMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public ServiceDTO updateServiceWithImage(Long id, ServiceDTO serviceDTO, MultipartFile image) throws IOException {
        log.info("Updating service with image, ID: {}", id);
        
        Service existing = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
        
        // Check if name is being changed to an existing name
        if (!existing.getName().equalsIgnoreCase(serviceDTO.getName()) &&
            serviceRepository.existsByNameIgnoreCase(serviceDTO.getName())) {
            throw new RuntimeException("Service with name '" + serviceDTO.getName() + "' already exists");
        }
        
        // Update image if provided
        if (image != null && !image.isEmpty()) {
            // Upload new image and delete old one
            Map<String, Object> uploadResult = fileStorageService.updateImage(image, existing.getImagePublicId());
            String imageUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            
            existing.setImageUrl(imageUrl);
            existing.setImagePublicId(publicId);
        }
        
        // Update fields
        existing.setName(serviceDTO.getName());
        existing.setDescription(serviceDTO.getDescription());
        existing.setPrice(serviceDTO.getPrice());
        existing.setEstimatedDurationMinutes(serviceDTO.getEstimatedDurationMinutes());
        if (serviceDTO.getActive() != null) {
            existing.setActive(serviceDTO.getActive());
        }
        
        Service updated = serviceRepository.save(existing);
        
        log.info("Service with image updated successfully: {}", updated.getId());
        return serviceMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        log.info("Deleting service with ID: {}", id);
        
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
        
        // Delete image from local storage if exists
        if (service.getImagePublicId() != null && !service.getImagePublicId().isEmpty()) {
            try {
                fileStorageService.deleteImage(service.getImagePublicId());
                log.info("Service image deleted from local storage: {}", service.getImagePublicId());
            } catch (IOException e) {
                log.error("Failed to delete service image from local storage, continuing with service deletion", e);
                // Continue with service deletion even if image deletion fails
            }
        }
        
        serviceRepository.deleteById(id);
        log.info("Service deleted successfully: {}", id);
    }

    @Override
    @Transactional
    public ServiceDTO toggleServiceStatus(Long id) {
        log.info("Toggling service status for ID: {}", id);
        
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
        
        service.setActive(!service.getActive());
        Service updated = serviceRepository.save(service);
        
        log.info("Service status toggled. ID: {}, New status: {}", id, updated.getActive());
        return serviceMapper.toDTO(updated);
    }
}
