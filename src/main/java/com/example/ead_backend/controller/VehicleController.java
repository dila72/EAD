package com.example.ead_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    // ========================= CUSTOMER-SPECIFIC ENDPOINTS
    // =========================

    /**
     * Customer creates their own vehicle (without image)
     * POST /api/vehicles/customer/my-vehicles
     */
    @PostMapping("/customer/my-vehicles")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createMyVehicle(
            @RequestBody VehicleDTO vehicleDTO,
            Authentication authentication) {
        try {
            VehicleDTO created = vehicleService.createVehicleForCustomer(vehicleDTO, authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Customer creates their own vehicle with image
     * POST /api/vehicles/customer/my-vehicles/with-image
     */
    @PostMapping(value = "/customer/my-vehicles/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createMyVehicleWithImage(
            @RequestPart("vehicle") String vehicleDTOJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        try {
            VehicleDTO vehicleDTO = objectMapper.readValue(vehicleDTOJson, VehicleDTO.class);

            VehicleDTO created;
            if (image != null && !image.isEmpty()) {
                created = vehicleService.createVehicleForCustomerWithImage(vehicleDTO, image, authentication);
            } else {
                created = vehicleService.createVehicleForCustomer(vehicleDTO, authentication);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create vehicle: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Customer gets all their own vehicles
     * GET /api/vehicles/customer/my-vehicles
     */
    @GetMapping("/customer/my-vehicles")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getMyVehicles(Authentication authentication) {
        try {
            List<VehicleDTO> vehicles = vehicleService.getVehiclesForCustomer(authentication);
            return ResponseEntity.ok(vehicles);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Customer gets a specific vehicle by ID (only if it belongs to them)
     * GET /api/vehicles/customer/my-vehicles/{vehicleId}
     */
    @GetMapping("/customer/my-vehicles/{vehicleId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getMyVehicleById(
            @PathVariable Long vehicleId,
            Authentication authentication) {
        try {
            VehicleDTO vehicle = vehicleService.getVehicleForCustomer(vehicleId, authentication);
            return ResponseEntity.ok(vehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Customer updates their own vehicle (without image)
     * PUT /api/vehicles/customer/my-vehicles/{vehicleId}
     */
    @PutMapping("/customer/my-vehicles/{vehicleId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateMyVehicle(
            @PathVariable Long vehicleId,
            @RequestBody VehicleDTO vehicleDTO,
            Authentication authentication) {
        try {
            VehicleDTO updated = vehicleService.updateVehicleForCustomer(vehicleId, vehicleDTO, authentication);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Customer updates their own vehicle with image
     * PUT /api/vehicles/customer/my-vehicles/{vehicleId}/with-image
     */
    @PutMapping(value = "/customer/my-vehicles/{vehicleId}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateMyVehicleWithImage(
            @PathVariable Long vehicleId,
            @RequestPart("vehicle") String vehicleDTOJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        try {
            VehicleDTO vehicleDTO = objectMapper.readValue(vehicleDTOJson, VehicleDTO.class);

            VehicleDTO updated;
            if (image != null && !image.isEmpty()) {
                updated = vehicleService.updateVehicleForCustomerWithImage(vehicleId, vehicleDTO, image,
                        authentication);
            } else {
                updated = vehicleService.updateVehicleForCustomer(vehicleId, vehicleDTO, authentication);
            }
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update vehicle: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Customer deletes their own vehicle
     * DELETE /api/vehicles/customer/my-vehicles/{vehicleId}
     */
    @DeleteMapping("/customer/my-vehicles/{vehicleId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> deleteMyVehicle(
            @PathVariable Long vehicleId,
            Authentication authentication) {
        try {
            vehicleService.deleteVehicleForCustomer(vehicleId, authentication);
            return ResponseEntity.ok("Vehicle deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
