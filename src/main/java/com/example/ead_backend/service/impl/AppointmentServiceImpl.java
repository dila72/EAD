package com.example.ead_backend.service.impl;

import com.example.ead_backend.service.AppointmentService;
import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.dto.EmployeeAvailabilityDTO;
import com.example.ead_backend.model.entity.Appointment;
import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.enums.AppointmentStatus;
import com.example.ead_backend.repository.AppointmentRepository;
import com.example.ead_backend.repository.EmployeeRepository;
import com.example.ead_backend.mapper.AppointmentMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        Appointment entity = appointmentMapper.toEntity(dto);
        // ensure new fields are copied (mapper should handle this if configured)
        entity.setCustomerId(dto.getCustomerId());
        entity.setVehicleId(dto.getVehicleId());
        
        // Set status to PENDING if not specified (customer appointments default to pending)
        if (entity.getStatus() == null) {
            entity.setStatus(AppointmentStatus.PENDING);
        }
        
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

    @Override
    public List<AppointmentDTO> getPendingAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.PENDING)
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentDTO assignAppointmentToEmployee(String appointmentId, Long employeeId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id " + appointmentId));
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id " + employeeId));
        
        // Assign the employee and update status
        appointment.setEmployee(employee);
        appointment.setStatus(AppointmentStatus.UPCOMING);
        
        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toDTO(updated);
    }

    @Override
    public List<EmployeeAvailabilityDTO> getAvailableEmployees(LocalDate date) {
        List<Employee> allEmployees = employeeRepository.findAll();
        
        return allEmployees.stream()
                .map(employee -> {
                    Long appointmentCount = appointmentRepository.countAppointmentsByEmployeeAndDate(
                            employee.getId(), date);
                    
                    String employeeName = employee.getUser() != null ? 
                            employee.getUser().getFirstName() + " " + employee.getUser().getLastName() : "Unknown";
                    
                    String email = employee.getUser() != null ? employee.getUser().getEmail() : "";
                    
                    // Consider an employee available if they have less than 5 appointments for that day
                    boolean available = appointmentCount < 5;
                    
                    return new EmployeeAvailabilityDTO(
                            employee.getId(),
                            employeeName,
                            email,
                            employee.getRole().name(),
                            appointmentCount.intValue(),
                            available
                    );
                })
                .collect(Collectors.toList());
    }
}
