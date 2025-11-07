package com.example.ead_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDate;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.dto.EmployeeAvailabilityDTO;

public interface AppointmentService {
    AppointmentDTO createAppointment(AppointmentDTO dto);

    AppointmentDTO getAppointmentById(String id);

    List<AppointmentDTO> getAllAppointments();

    List<AppointmentDTO> getAppointmentsByCustomerId(String customerId);
    
    List<AppointmentDTO> getAppointmentsByEmployeeId(Long employeeId);

    AppointmentDTO updateAppointment(String id, AppointmentDTO dto);
    
    AppointmentDTO assignEmployeeToAppointment(String appointmentId, Long employeeId);

    void deleteAppointment(String id);

    List<String> getBookedStartTimes(LocalDate date);
}
