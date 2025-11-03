package com.example.ead_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.ead_backend.dto.VehicleDTO;
import com.example.ead_backend.model.entity.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    VehicleDTO toDTO(Vehicle vehicle);

    Vehicle toEntity(VehicleDTO dto);
}
