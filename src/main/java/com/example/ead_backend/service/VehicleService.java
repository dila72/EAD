package com.example.ead_backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.example.ead_backend.dto.VehicleDTO;

public interface VehicleService {
    VehicleDTO createVehicle(VehicleDTO vehicleDTO);

    VehicleDTO createVehicleWithImage(VehicleDTO vehicleDTO, MultipartFile image) throws IOException;

    VehicleDTO getVehicleById(Long id);

    List<VehicleDTO> getAllVehicles();

    List<VehicleDTO> getVehiclesByCustomerId(Long customerId);

    VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO);

    VehicleDTO updateVehicleWithImage(Long id, VehicleDTO vehicleDTO, MultipartFile image) throws IOException;

    void deleteVehicle(Long id);

    // Customer-specific methods
    VehicleDTO createVehicleForCustomer(VehicleDTO vehicleDTO, Authentication authentication);

    VehicleDTO createVehicleForCustomerWithImage(VehicleDTO vehicleDTO, MultipartFile image,
            Authentication authentication) throws IOException;

    List<VehicleDTO> getVehiclesForCustomer(Authentication authentication);

    VehicleDTO getVehicleForCustomer(Long vehicleId, Authentication authentication);

    VehicleDTO updateVehicleForCustomer(Long vehicleId, VehicleDTO vehicleDTO, Authentication authentication);

    VehicleDTO updateVehicleForCustomerWithImage(Long vehicleId, VehicleDTO vehicleDTO, MultipartFile image,
            Authentication authentication) throws IOException;

    void deleteVehicleForCustomer(Long vehicleId, Authentication authentication);
}
