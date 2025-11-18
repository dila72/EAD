package com.example.ead_backend.service.impl;

import com.example.ead_backend.service.ProjectService;
import com.example.ead_backend.service.ProgressCalculationService;
import com.example.ead_backend.dto.ProjectDTO;
import com.example.ead_backend.model.entity.Project;
import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.repository.ProjectRepository;
import com.example.ead_backend.repository.EmployeeRepository;
import com.example.ead_backend.mapper.ProjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectMapper projectMapper;
    private final ProgressCalculationService progressCalculationService;

    @Override
    public ProjectDTO createProject(ProjectDTO dto) {
        Project entity = projectMapper.toEntity(dto);
        // Set default status to REQUESTING for new projects
        entity.setStatus(com.example.ead_backend.model.enums.ProjectStatus.REQUESTING);
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
                .map(project -> {
                    ProjectDTO dto = projectMapper.toDTO(project);
                    // Populate progress percentage from progress tracking system
                    try {
                        int percentage = progressCalculationService.getLatestProgress(project.getProjectId());
                        dto.setProgressPercentage(percentage);
                    } catch (Exception e) {
                        // If no progress data exists, default to 0
                        dto.setProgressPercentage(0);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getProjectsByCustomerId(String customerId) {
        return projectRepository.findByCustomerId(customerId)
                .stream()
                .map(project -> {
                    ProjectDTO dto = projectMapper.toDTO(project);
                    // Populate progress percentage from progress tracking system
                    try {
                        int percentage = progressCalculationService.getLatestProgress(project.getProjectId());
                        dto.setProgressPercentage(percentage);
                    } catch (Exception e) {
                        // If no progress data exists, default to 0
                        dto.setProgressPercentage(0);
                    }
                    return dto;
                })
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

    @Override
    public ProjectDTO assignEmployeeToProject(String projectId, Long employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id " + employeeId));
        
        project.setEmployee(employee);
        // Change status from REQUESTING to ASSIGNED when employee is assigned
        project.setStatus(com.example.ead_backend.model.enums.ProjectStatus.ASSIGNED);
        Project updated = projectRepository.save(project);
        return projectMapper.toDTO(updated);
    }

    @Override
    public List<ProjectDTO> getProjectsByEmployeeId(Long employeeId) {
        return projectRepository.findByEmployeeId(employeeId)
                .stream()
                .map(project -> {
                    ProjectDTO dto = projectMapper.toDTO(project);
                    // Populate progress percentage from progress tracking system
                    try {
                        int percentage = progressCalculationService.getLatestProgress(project.getProjectId());
                        dto.setProgressPercentage(percentage);
                    } catch (Exception e) {
                        // If no progress data exists, default to 0
                        dto.setProgressPercentage(0);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
