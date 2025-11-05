package com.example.ead_backend.service;

import java.util.List;

import com.example.ead_backend.dto.ProjectDTO;

public interface ProjectService {
    ProjectDTO createProject(ProjectDTO dto);

    ProjectDTO getProjectById(String id);

    List<ProjectDTO> getAllProjects();

    List<ProjectDTO> getProjectsByCustomerId(String customerId);

    ProjectDTO updateProject(String id, ProjectDTO dto);

    void deleteProject(String id);
}
