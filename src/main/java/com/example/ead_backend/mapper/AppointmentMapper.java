package com.example.ead_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.model.entity.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", expression = "java(getEmployeeName(entity))")
    AppointmentDTO toDTO(Appointment entity);
    
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "time", ignore = true)
    Appointment toEntity(AppointmentDTO dto);

    default String getEmployeeName(Appointment entity) {
        if (entity != null && entity.getEmployee() != null && entity.getEmployee().getUser() != null) {
            return entity.getEmployee().getUser().getFirstName() + " " + entity.getEmployee().getUser().getLastName();
        }
        return null;
    }

    @AfterMapping
    default void setLegacyTime(@MappingTarget Appointment entity, AppointmentDTO dto) {
        if (entity == null) return;
        String start = dto != null ? dto.getStartTime() : null;
        String end = dto != null ? dto.getEndTime() : null;
        String derived = (start != null && end != null)
                ? start + "-" + end
                : (start != null ? start : "00:00");
        entity.setTime(derived);
    }
}
