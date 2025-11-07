package com.example.ead_backend.mapper;

import com.example.ead_backend.dto.VehicleDTO;
import com.example.ead_backend.model.entity.Vehicle;
import com.example.ead_backend.model.entity.Customer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class VehicleMapperManual implements VehicleMapper {

    @Override
    public VehicleDTO toDTO(Vehicle vehicle) {
        if (vehicle == null) return null;
        VehicleDTO dto = VehicleDTO.builder()
                .id(vehicle.getId())
                .model(vehicle.getModel())
                .color(vehicle.getColor())
                .vin(vehicle.getVin())
                .licensePlate(vehicle.getLicensePlate())
                .year(vehicle.getYear())
                .registrationDate(vehicle.getRegistrationDate())
                .imageUrl(vehicle.getImageUrl())
                .build();
        Customer customer = vehicle.getCustomer();
        if (customer != null) {
            dto.setCustomerId(customer.getId());
        }
        return dto;
    }

    @Override
    public Vehicle toEntity(VehicleDTO dto) {
        if (dto == null) return null;
        Vehicle entity = Vehicle.builder()
                .id(dto.getId())
                .model(dto.getModel())
                .color(dto.getColor())
                .vin(dto.getVin())
                .licensePlate(dto.getLicensePlate())
                .year(dto.getYear())
                .registrationDate(dto.getRegistrationDate())
                .imageUrl(dto.getImageUrl())
                .build();
        // customer is set in service layer after lookup
        return entity;
    }
}
