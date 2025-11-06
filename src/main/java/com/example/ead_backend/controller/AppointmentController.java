package com.example.ead_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.dto.AssignAppointmentRequest;
import com.example.ead_backend.dto.EmployeeAvailabilityDTO;
import com.example.ead_backend.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;
import java.security.Principal;

@RestController
@Slf4j
@RequestMapping("/api/appointments") 
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    // Helper to get current user's ID (you can customize this based on your auth setup)
    private String getCurrentUserId(Principal principal) {
        return principal != null ? principal.getName() : null;
    }

    @PostMapping
    public AppointmentDTO create(@RequestBody AppointmentDTO dto, Principal principal) {
        // Set customerId from authenticated user when creating
        String userId = getCurrentUserId(principal);
        if (userId != null) {
            dto.setCustomerId(userId);
        }
        return appointmentService.createAppointment(dto);
    }

    @GetMapping("/{id}")
    public AppointmentDTO getById(@PathVariable String id, Principal principal) {
        // Check if the user owns the appointment
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        String userId = getCurrentUserId(principal);
        if (userId != null && !userId.equals(appointment.getCustomerId())) {
            throw new RuntimeException("Not authorized to view this appointment");
        }
        return appointment;
    }

    @GetMapping
    public List<AppointmentDTO> getAll(@RequestParam(required = false) String customerId, Principal principal) {
        // If no customerId provided but user is authenticated, use their ID
        if (customerId == null && principal != null) {
            customerId = getCurrentUserId(principal);
        }
        
        // If customerId is provided (either via param or auth), filter by it
        if (customerId != null) {
            return appointmentService.getAppointmentsByCustomerId(customerId);
        }
        
        // Otherwise return all appointments (e.g., for admin users)
        return appointmentService.getAllAppointments();
    }

    @PutMapping("/{id}")
    public AppointmentDTO update(@PathVariable String id, @RequestBody AppointmentDTO dto, Principal principal) {
        // Verify user owns the appointment they're trying to update
        AppointmentDTO existing = appointmentService.getAppointmentById(id);
        String userId = getCurrentUserId(principal);
        if (userId != null && !userId.equals(existing.getCustomerId())) {
            throw new RuntimeException("Not authorized to update this appointment");
        }

        // Preserve the existing customerId - don't allow it to be changed
        dto.setCustomerId(existing.getCustomerId());
        return appointmentService.updateAppointment(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, Principal principal) {
        // Log incoming delete attempt for debugging
        log.info("DELETE /api/appointments/{} called, principal={}", id, principal == null ? "<anonymous>" : principal.getName());

        // Verify user owns the appointment they're trying to delete
        AppointmentDTO existing = appointmentService.getAppointmentById(id);
        String userId = getCurrentUserId(principal);
        if (userId != null && !userId.equals(existing.getCustomerId())) {
            log.warn("User {} not authorized to delete appointment {} owned by {}", userId, id, existing.getCustomerId());
            throw new RuntimeException("Not authorized to delete this appointment");
        }

        appointmentService.deleteAppointment(id);
        log.info("Appointment {} deleted successfully", id);
    }
    
    // ==================== ADMIN ENDPOINTS ====================
    
    /**
     * Get all pending appointments that need to be assigned to employees
     * Only accessible by admins
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDTO>> getPendingAppointments() {
        log.info("Admin fetching pending appointments");
        List<AppointmentDTO> pendingAppointments = appointmentService.getPendingAppointments();
        return ResponseEntity.ok(pendingAppointments);
    }
    
    /**
     * Get available employees for a specific date
     * Returns employee information with their current appointment count
     */
    @GetMapping("/available-employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeAvailabilityDTO>> getAvailableEmployees(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Admin fetching available employees for date: {}", date);
        List<EmployeeAvailabilityDTO> employees = appointmentService.getAvailableEmployees(date);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Assign a pending appointment to an employee
     * Changes appointment status from PENDING to UPCOMING
     */
    @PutMapping("/{appointmentId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentDTO> assignAppointment(
            @PathVariable String appointmentId,
            @RequestBody AssignAppointmentRequest request) {
        log.info("Admin assigning appointment {} to employee {}", appointmentId, request.getEmployeeId());
        
        AppointmentDTO assigned = appointmentService.assignAppointmentToEmployee(
                appointmentId, request.getEmployeeId());
        
        return ResponseEntity.ok(assigned);
    }
}
