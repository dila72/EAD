package com.example.ead_backend.mapper;

import com.example.ead_backend.dto.ProjectDTO;
import com.example.ead_backend.model.entity.Project;

// Fallback manual mapper (not a Spring bean). Kept only to avoid compile issues
// when MapStruct processing is unavailable. MapStruct will generate the real bean.
class ProjectMapperManual implements ProjectMapper {

    @Override
    public ProjectDTO toDTO(Project entity) {
        if (entity == null) return null;
        ProjectDTO dto = new ProjectDTO();
        dto.setProjectId(entity.getProjectId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCustomerId(entity.getCustomerId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    @Override
    public Project toEntity(ProjectDTO dto) {
        if (dto == null) return null;
        Project entity = new Project();
        entity.setProjectId(dto.getProjectId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCustomerId(dto.getCustomerId());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
