package com.example.ead_backend.mapper;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.model.entity.Appointment;
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
        return entity;
    }
}
