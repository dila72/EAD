package com.example.ead_backend.mapper;

import com.example.ead_backend.dto.ServiceDTO;
import com.example.ead_backend.model.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Service entity and ServiceDTO conversion
 */
@Mapper(componentModel = "spring")
public interface ServiceMapper {
    
    /**
     * Convert Service entity to ServiceDTO
     */
    ServiceDTO toDTO(Service service);
    
    /**
     * Convert ServiceDTO to Service entity
     * Note: imagePublicId is not exposed in DTO as it's internal to Cloudinary management
     */
    @Mapping(target = "imagePublicId", ignore = true)
    Service toEntity(ServiceDTO dto);
}
