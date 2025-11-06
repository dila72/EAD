package com.example.ead_backend.service;

import java.time.LocalDate;
import java.util.List;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.dto.EmployeeAvailabilityDTO;

public interface AppointmentService {
    AppointmentDTO createAppointment(AppointmentDTO dto);

    AppointmentDTO getAppointmentById(String id);

    List<AppointmentDTO> getAllAppointments();

    List<AppointmentDTO> getAppointmentsByCustomerId(String customerId);

    AppointmentDTO updateAppointment(String id, AppointmentDTO dto);

    void deleteAppointment(String id);
    
    // Admin functions for appointment management
    List<AppointmentDTO> getPendingAppointments();
    
    AppointmentDTO assignAppointmentToEmployee(String appointmentId, Long employeeId);
    
    List<EmployeeAvailabilityDTO> getAvailableEmployees(LocalDate date);
}
