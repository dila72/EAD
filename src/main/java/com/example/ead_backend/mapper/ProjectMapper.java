package com.example.ead_backend.mapper;

import org.mapstruct.Mapper;

import com.example.ead_backend.dto.ProjectDTO;
import com.example.ead_backend.model.entity.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDTO toDTO(Project entity);
    Project toEntity(ProjectDTO dto);
}
