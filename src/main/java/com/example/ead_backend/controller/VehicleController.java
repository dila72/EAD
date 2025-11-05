package com.example.ead_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.ead_backend.dto.VehicleDTO;
import com.example.ead_backend.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public VehicleDTO createVehicle(@RequestBody VehicleDTO vehicleDTO) {
        return vehicleService.createVehicle(vehicleDTO);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createVehicleWithImage(
            @RequestPart("vehicle") String vehicleDTOJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            VehicleDTO vehicleDTO = objectMapper.readValue(vehicleDTOJson, VehicleDTO.class);
            
            if (image != null && !image.isEmpty()) {
                VehicleDTO created = vehicleService.createVehicleWithImage(vehicleDTO, image);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
            } else {
                VehicleDTO created = vehicleService.createVehicle(vehicleDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create vehicle: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public VehicleDTO getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @GetMapping
    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/customer/{customerId}")
    public List<VehicleDTO> getVehiclesByCustomerId(@PathVariable Long customerId) {
        return vehicleService.getVehiclesByCustomerId(customerId);
    }

    @PutMapping("/{id}")
    public VehicleDTO updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {
        return vehicleService.updateVehicle(id, vehicleDTO);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateVehicleWithImage(
            @PathVariable Long id,
            @RequestPart("vehicle") String vehicleDTOJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            VehicleDTO vehicleDTO = objectMapper.readValue(vehicleDTOJson, VehicleDTO.class);
            
            if (image != null && !image.isEmpty()) {
                VehicleDTO updated = vehicleService.updateVehicleWithImage(id, vehicleDTO, image);
                return ResponseEntity.ok(updated);
            } else {
                VehicleDTO updated = vehicleService.updateVehicle(id, vehicleDTO);
                return ResponseEntity.ok(updated);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update vehicle: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok("Vehicle deleted successfully");
    }
}
