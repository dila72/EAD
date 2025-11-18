package com.example.ead_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.example.ead_backend.dto.ProjectDTO;
import com.example.ead_backend.service.ProjectService;

import java.util.List;
import java.security.Principal;

@RestController
@Slf4j
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    private String getCurrentUserId(Principal principal) {
        return principal != null ? principal.getName() : null;
    }

    @PostMapping
    public ProjectDTO create(@RequestBody ProjectDTO dto, Principal principal) {
        String userId = getCurrentUserId(principal);
        if (userId != null) {
            dto.setCustomerId(userId);
        }
        return projectService.createProject(dto);
    }

    @GetMapping("/{id}")
    public ProjectDTO getById(@PathVariable String id, Principal principal) {
        ProjectDTO project = projectService.getProjectById(id);
        String userId = getCurrentUserId(principal);
        if (userId != null && !userId.equals(project.getCustomerId())) {
            throw new RuntimeException("Not authorized to view this project");
        }
        return project;
    }

    @GetMapping
    public List<ProjectDTO> getAll(@RequestParam(required = false) String customerId, Principal principal) {
        if (customerId == null && principal != null) {
            customerId = getCurrentUserId(principal);
        }
        if (customerId != null) {
            return projectService.getProjectsByCustomerId(customerId);
        }
        return projectService.getAllProjects();
    }

    @PutMapping("/{id}")
    public ProjectDTO update(@PathVariable String id, @RequestBody ProjectDTO dto, Principal principal) {
        ProjectDTO existing = projectService.getProjectById(id);
        String userId = getCurrentUserId(principal);
        if (userId != null && !userId.equals(existing.getCustomerId())) {
            throw new RuntimeException("Not authorized to update this project");
        }
        dto.setCustomerId(existing.getCustomerId());
        return projectService.updateProject(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, Principal principal) {
        log.info("DELETE /api/projects/{} called, principal={}", id, principal == null ? "<anonymous>" : principal.getName());
        ProjectDTO existing = projectService.getProjectById(id);
        String userId = getCurrentUserId(principal);
        if (userId != null && !userId.equals(existing.getCustomerId())) {
            log.warn("User {} not authorized to delete project {} owned by {}", userId, id, existing.getCustomerId());
            throw new RuntimeException("Not authorized to delete this project");
        }
        projectService.deleteProject(id);
        log.info("Project {} deleted successfully", id);
    }

    @PutMapping("/{id}/assign-employee")
    public ProjectDTO assignEmployee(@PathVariable String id, @RequestBody java.util.Map<String, Object> request) {
        Object employeeIdObj = request.get("employeeId");
        if (employeeIdObj == null) {
            log.error("Employee ID is null in request: {}", request);
            throw new RuntimeException("Employee ID is required");
        }
        
        Long employeeId;
        if (employeeIdObj instanceof Number) {
            employeeId = ((Number) employeeIdObj).longValue();
        } else if (employeeIdObj instanceof String) {
            try {
                employeeId = Long.parseLong((String) employeeIdObj);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid employee ID format: " + employeeIdObj);
            }
        } else {
            throw new RuntimeException("Invalid employee ID type: " + employeeIdObj.getClass().getName());
        }
        
        log.info("Assigning employee {} to project {}", employeeId, id);
        return projectService.assignEmployeeToProject(id, employeeId);
    }
}
