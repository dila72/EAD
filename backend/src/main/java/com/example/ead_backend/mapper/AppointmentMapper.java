package com.example.ead_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.model.entity.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentDTO toDTO(Appointment entity);
    Appointment toEntity(AppointmentDTO dto);

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
