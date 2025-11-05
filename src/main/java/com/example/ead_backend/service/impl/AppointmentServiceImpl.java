package com.example.ead_backend.service.impl;

import com.example.ead_backend.service.AppointmentService;
import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.model.entity.Appointment;
import com.example.ead_backend.repository.AppointmentRepository;
import com.example.ead_backend.mapper.AppointmentMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        Appointment entity = appointmentMapper.toEntity(dto);
        // ensure new fields are copied (mapper should handle this if configured)
        entity.setCustomerId(dto.getCustomerId());
        entity.setVehicleId(dto.getVehicleId());
        Appointment saved = appointmentRepository.save(entity);
        return appointmentMapper.toDTO(saved);
    }

    @Override
    public AppointmentDTO getAppointmentById(String id) {
        Appointment entity = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id " + id));
        return appointmentMapper.toDTO(entity);
    }

    @Override
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByCustomerId(String customerId) {
        return appointmentRepository.findByCustomerId(customerId)
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO updateAppointment(String id, AppointmentDTO dto) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id " + id));

        existing.setService(dto.getService());
    existing.setCustomerId(dto.getCustomerId());
    existing.setVehicleId(dto.getVehicleId());
        existing.setVehicleNo(dto.getVehicleNo());
        existing.setDate(dto.getDate());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        // Update legacy 'time' to satisfy NOT NULL constraint
        String start = dto.getStartTime();
        String end = dto.getEndTime();
        String derived = (start != null && end != null) ? start + "-" + end : (start != null ? start : "00:00");
        existing.setTime(derived);
        existing.setStatus(dto.getStatus());

        Appointment updated = appointmentRepository.save(existing);
        return appointmentMapper.toDTO(updated);
    }

    @Override
    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }
}
