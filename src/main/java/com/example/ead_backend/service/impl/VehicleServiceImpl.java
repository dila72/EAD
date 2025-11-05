package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.VehicleDTO;
import com.example.ead_backend.mapper.VehicleMapper;
import com.example.ead_backend.model.entity.Customer;
import com.example.ead_backend.model.entity.Vehicle;
import com.example.ead_backend.repository.CustomerRepository;
import com.example.ead_backend.repository.VehicleRepository;
import com.example.ead_backend.service.CloudinaryService;
import com.example.ead_backend.service.VehicleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok: creates a constructor for final fields (auto-injects them)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleMapper vehicleMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        // Find the customer
        Customer customer = customerRepository.findById(vehicleDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + vehicleDTO.getCustomerId()));

        // Create vehicle entity
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        vehicle.setCustomer(customer);

        // Save and return
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toDTO(saved);
    }

    @Override
    public VehicleDTO createVehicleWithImage(VehicleDTO vehicleDTO, MultipartFile image) throws IOException {
        // Find the customer
        Customer customer = customerRepository.findById(vehicleDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + vehicleDTO.getCustomerId()));

        // Upload image to Cloudinary
        Map<String, Object> uploadResult = cloudinaryService.uploadImage(image);
        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        // Create vehicle entity
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        vehicle.setCustomer(customer);
        vehicle.setImageUrl(imageUrl);
        vehicle.setImagePublicId(publicId);

        // Save and return
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toDTO(saved);
    }

    @Override
    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id " + id));
        return vehicleMapper.toDTO(vehicle);
    }

    @Override
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(vehicleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> getVehiclesByCustomerId(Long customerId) {
        // Verify customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));

        return vehicleRepository.findByCustomerId(customerId)
                .stream()
                .map(vehicleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id " + id));

        // Update fields
        existing.setModel(vehicleDTO.getModel());
        existing.setColor(vehicleDTO.getColor());
        existing.setVin(vehicleDTO.getVin());
        existing.setLicensePlate(vehicleDTO.getLicensePlate());
        existing.setYear(vehicleDTO.getYear());
        existing.setRegistrationDate(vehicleDTO.getRegistrationDate());

        // If customer is being changed
        if (vehicleDTO.getCustomerId() != null && !vehicleDTO.getCustomerId().equals(existing.getCustomer().getId())) {
            Customer newCustomer = customerRepository.findById(vehicleDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id " + vehicleDTO.getCustomerId()));
            existing.setCustomer(newCustomer);
        }

        Vehicle updated = vehicleRepository.save(existing);
        return vehicleMapper.toDTO(updated);
    }

    @Override
    public VehicleDTO updateVehicleWithImage(Long id, VehicleDTO vehicleDTO, MultipartFile image) throws IOException {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id " + id));

        // Update image if provided
        if (image != null && !image.isEmpty()) {
            // Upload new image and delete old one
            Map<String, Object> uploadResult = cloudinaryService.updateImage(image, existing.getImagePublicId());
            String imageUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            existing.setImageUrl(imageUrl);
            existing.setImagePublicId(publicId);
        }

        // Update fields
        existing.setModel(vehicleDTO.getModel());
        existing.setColor(vehicleDTO.getColor());
        existing.setVin(vehicleDTO.getVin());
        existing.setLicensePlate(vehicleDTO.getLicensePlate());
        existing.setYear(vehicleDTO.getYear());
        existing.setRegistrationDate(vehicleDTO.getRegistrationDate());

        // If customer is being changed
        if (vehicleDTO.getCustomerId() != null && !vehicleDTO.getCustomerId().equals(existing.getCustomer().getId())) {
            Customer newCustomer = customerRepository.findById(vehicleDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id " + vehicleDTO.getCustomerId()));
            existing.setCustomer(newCustomer);
        }

        Vehicle updated = vehicleRepository.save(existing);
        return vehicleMapper.toDTO(updated);
    }

    @Override
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id " + id));

        // Delete image from Cloudinary if exists
        if (vehicle.getImagePublicId() != null && !vehicle.getImagePublicId().isEmpty()) {
            try {
                cloudinaryService.deleteImage(vehicle.getImagePublicId());
            } catch (IOException e) {
                // Log error but continue with vehicle deletion
                // You might want to handle this differently based on your requirements
            }
        }

        vehicleRepository.deleteById(id);
    }
}
