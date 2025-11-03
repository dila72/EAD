package com.example.ead_backend.service;

import java.util.List;

import com.example.ead_backend.dto.VehicleDTO;

public interface VehicleService {
    VehicleDTO createVehicle(VehicleDTO vehicleDTO);

    VehicleDTO getVehicleById(Long id);

    List<VehicleDTO> getAllVehicles();

    VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO);

    void deleteVehicle(Long id);
}
