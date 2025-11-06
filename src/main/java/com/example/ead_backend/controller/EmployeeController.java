package com.example.ead_backend.controller;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.repository.EmployeeRepository;
import com.example.ead_backend.repository.UserRepo;
import com.example.ead_backend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final AppointmentService appointmentService;
    private final EmployeeRepository employeeRepository;
    private final UserRepo userRepository;

    @GetMapping("/appointments")
    public List<AppointmentDTO> getMyAppointments(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        String email = principal.getName();
        log.info("Fetching appointments for employee with email: {}", email);
        
        // Find user by email, then get employee
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        Employee employee = user.getEmployee();
        if (employee == null) {
            throw new RuntimeException("Employee profile not found for user: " + email);
        }
        
        return appointmentService.getAppointmentsByEmployeeId(employee.getId());
    }

    @GetMapping("/appointments/{status}")
    public List<AppointmentDTO> getAppointmentsByStatus(@PathVariable String status, Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        Employee employee = user.getEmployee();
        if (employee == null) {
            throw new RuntimeException("Employee profile not found for user: " + email);
        }
        
        log.info("Fetching {} appointments for employee {}", status, employee.getId());
        return appointmentService.getAppointmentsByEmployeeId(employee.getId()).stream()
                .filter(apt -> status.equalsIgnoreCase(apt.getStatus().name()))
                .toList();
    }
}
