package com.example.ead_backend.service;

import java.io.IOException;
import java.util.List;

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
}
