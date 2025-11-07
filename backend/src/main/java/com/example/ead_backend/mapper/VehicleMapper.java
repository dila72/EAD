package com.example.ead_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.ead_backend.dto.VehicleDTO;
import com.example.ead_backend.model.entity.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    @Mapping(source = "customer.id", target = "customerId")
    VehicleDTO toDTO(Vehicle vehicle);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "imagePublicId", ignore = true)
    Vehicle toEntity(VehicleDTO dto);
}
