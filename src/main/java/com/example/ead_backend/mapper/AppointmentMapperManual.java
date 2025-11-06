package com.example.ead_backend.mapper;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.dto.EmployeeDTO;
import com.example.ead_backend.model.entity.Appointment;
import com.example.ead_backend.model.entity.Employee;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

// Fallback manual mapper (not a Spring bean). Kept only to avoid compile issues
// when MapStruct processing is unavailable. MapStruct will generate the real bean.
@Primary
@Component
public class AppointmentMapperManual implements AppointmentMapper {

    @Override
    public AppointmentDTO toDTO(Appointment entity) {
        if (entity == null) return null;
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentId(entity.getAppointmentId());
        dto.setService(entity.getService());
        dto.setCustomerId(entity.getCustomerId());
        dto.setVehicleId(entity.getVehicleId());
        dto.setVehicleNo(entity.getVehicleNo());
        dto.setDate(entity.getDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
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
    public Appointment toEntity(AppointmentDTO dto) {
        if (dto == null) return null;
        Appointment entity = new Appointment();
        entity.setAppointmentId(dto.getAppointmentId());
        entity.setService(dto.getService());
        entity.setCustomerId(dto.getCustomerId());
        entity.setVehicleId(dto.getVehicleId());
        entity.setVehicleNo(dto.getVehicleNo());
        entity.setDate(dto.getDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        // Set legacy 'time' column as a derived range to satisfy NOT NULL
        String start = dto.getStartTime();
        String end = dto.getEndTime();
        entity.setTime((start != null && end != null) ? start + "-" + end : (start != null ? start : "00:00"));
        entity.setStatus(dto.getStatus());
        
        // Note: Employee mapping is typically handled by the service layer
        // when assigning employees to appointments
        
        return entity;
    }
}
