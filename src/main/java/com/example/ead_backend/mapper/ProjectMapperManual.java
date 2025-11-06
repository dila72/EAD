package com.example.ead_backend.mapper;

import com.example.ead_backend.dto.ProjectDTO;
import com.example.ead_backend.dto.EmployeeDTO;
import com.example.ead_backend.model.entity.Project;
import com.example.ead_backend.model.entity.Employee;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

// Fallback manual mapper (not a Spring bean). Kept only to avoid compile issues
// when MapStruct processing is unavailable. MapStruct will generate the real bean.
@Primary
@Component
public class ProjectMapperManual implements ProjectMapper {

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
        
        // Map employee if present
        if (entity.getEmployee() != null) {
            Employee emp = entity.getEmployee();
            EmployeeDTO empDto = new EmployeeDTO();
            empDto.setId(emp.getId());
            empDto.setRole(emp.getRole().name());
            empDto.setJoinedDate(emp.getJoinedDate());
            
            // Get user details from the employee's user
            if (emp.getUser() != null) {
                empDto.setFirstName(emp.getUser().getFirstName());
                empDto.setLastName(emp.getUser().getLastName());
                empDto.setEmail(emp.getUser().getEmail());
            }
            
            dto.setEmployee(empDto);
        }
        
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
        
        // Note: Employee mapping is typically handled by the service layer
        // when assigning employees to projects
        
        return entity;
    }
}
