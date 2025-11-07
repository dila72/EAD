package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.VehicleDTO;
import com.example.ead_backend.mapper.VehicleMapper;
import com.example.ead_backend.model.entity.Customer;
import com.example.ead_backend.model.entity.Vehicle;
import com.example.ead_backend.repository.CustomerRepository;
import com.example.ead_backend.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Customer testCustomer;
    private Vehicle testVehicle;
    private VehicleDTO testVehicleDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setPhoneNumber("1234567890");

        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setModel("Toyota Camry");
        testVehicle.setColor("Blue");
        testVehicle.setCustomer(testCustomer);

        testVehicleDTO = new VehicleDTO();
        testVehicleDTO.setId(1L);
        testVehicleDTO.setModel("Toyota Camry");
        testVehicleDTO.setColor("Blue");
        testVehicleDTO.setCustomerId(1L);
    }

    @Test
    void testCreateVehicle_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(vehicleMapper.toEntity(any(VehicleDTO.class))).thenReturn(testVehicle);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(vehicleMapper.toDTO(any(Vehicle.class))).thenReturn(testVehicleDTO);

        VehicleDTO result = vehicleService.createVehicle(testVehicleDTO);

        assertNotNull(result);
        assertEquals("Toyota Camry", result.getModel());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testCreateVehicle_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            vehicleService.createVehicle(testVehicleDTO);
        });

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void testGetVehicleById_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleMapper.toDTO(any(Vehicle.class))).thenReturn(testVehicleDTO);

        VehicleDTO result = vehicleService.getVehicleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    void testGetVehicleById_NotFound_ThrowsException() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            vehicleService.getVehicleById(999L);
        });
    }

    @Test
    void testGetAllVehicles_Success() {
        List<Vehicle> vehicles = Arrays.asList(testVehicle);
        when(vehicleRepository.findAll()).thenReturn(vehicles);
        when(vehicleMapper.toDTO(any(Vehicle.class))).thenReturn(testVehicleDTO);

        List<VehicleDTO> result = vehicleService.getAllVehicles();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    void testGetVehiclesByCustomerId_Success() {
        List<Vehicle> vehicles = Arrays.asList(testVehicle);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByCustomerId(1L)).thenReturn(vehicles);
        when(vehicleMapper.toDTO(any(Vehicle.class))).thenReturn(testVehicleDTO);

        List<VehicleDTO> result = vehicleService.getVehiclesByCustomerId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(vehicleRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void testDeleteVehicle_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        doNothing().when(vehicleRepository).deleteById(1L);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository, times(1)).deleteById(1L);
    }
}
