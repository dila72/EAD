package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.VehicleDTO;
import com.example.ead_backend.mapper.VehicleMapper;
import com.example.ead_backend.model.entity.Vehicle;
import com.example.ead_backend.repository.VehicleRepository;
import com.example.ead_backend.service.VehicleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok: creates a constructor for final fields (auto-injects them)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    public com.example.ead_backend.dto.VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
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
    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id " + id));

        existing.setModel(vehicleDTO.getModel());
        existing.setColor(vehicleDTO.getColor());
        existing.setLicensePlate(vehicleDTO.getLicensePlate());
        existing.setYear(vehicleDTO.getYear());
        existing.setRegistrationDate(vehicleDTO.getRegistrationDate());

        Vehicle updated = vehicleRepository.save(existing);
        return vehicleMapper.toDTO(updated);
    }

    @Override
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}
