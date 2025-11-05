package com.example.ead_backend.service.impl;

import com.example.ead_backend.service.ProjectService;
import com.example.ead_backend.dto.ProjectDTO;
import com.example.ead_backend.model.entity.Project;
import com.example.ead_backend.repository.ProjectRepository;
import com.example.ead_backend.mapper.ProjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDTO createProject(ProjectDTO dto) {
        Project entity = projectMapper.toEntity(dto);
        Project saved = projectRepository.save(entity);
        return projectMapper.toDTO(saved);
    }

    @Override
    public ProjectDTO getProjectById(String id) {
        Project entity = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + id));
        return projectMapper.toDTO(entity);
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getProjectsByCustomerId(String customerId) {
        return projectRepository.findByCustomerId(customerId)
                .stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO updateProject(String id, ProjectDTO dto) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setCustomerId(dto.getCustomerId());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setStatus(dto.getStatus());

        Project updated = projectRepository.save(existing);
        return projectMapper.toDTO(updated);
    }

    @Override
    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }
}
