package com.example.ead_backend.service;

import java.util.List;

import com.example.ead_backend.dto.AppointmentDTO;

public interface AppointmentService {
    AppointmentDTO createAppointment(AppointmentDTO dto);

    AppointmentDTO getAppointmentById(String id);

    List<AppointmentDTO> getAllAppointments();

    List<AppointmentDTO> getAppointmentsByCustomerId(String customerId);

    AppointmentDTO updateAppointment(String id, AppointmentDTO dto);

    void deleteAppointment(String id);
}
