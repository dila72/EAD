package com.example.ead_backend.mapper;

import com.example.ead_backend.dto.ProgressResponse;
import com.example.ead_backend.dto.ProgressUpdateRequest;
import com.example.ead_backend.model.entity.ProgressUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for ProgressUpdate entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface ProgressMapper {

    /**
     * Convert ProgressUpdate entity to ProgressResponse DTO.
     *
     * @param entity the progress update entity
     * @return the progress response DTO
     */
    ProgressResponse toResponse(ProgressUpdate entity);

    /**
     * Convert ProgressUpdateRequest DTO to ProgressUpdate entity.
     * Note: appointmentId and updatedBy must be set separately.
     *
     * @param dto the progress update request
     * @return the progress update entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointmentId", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProgressUpdate toEntity(ProgressUpdateRequest dto);
}
