package com.example.ead_backend.controller;

import com.example.ead_backend.dto.ProgressResponse;
import com.example.ead_backend.service.ProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for customer progress view operations.
 */
@RestController
@RequestMapping("/api/customer/progress")
@RequiredArgsConstructor
@Slf4j
public class ProgressViewController {

    private final ProgressService progressService;

    /**
     * Get all progress updates for an appointment.
     *
     * @param appointmentId the appointment ID
     * @return list of progress responses
     */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<List<ProgressResponse>> getProgressHistory(
            @PathVariable Long appointmentId) {
        
        log.info("Fetching progress history for appointment {}", appointmentId);
        
        List<ProgressResponse> progressList = progressService.getProgressForAppointment(appointmentId);
        
        return ResponseEntity.ok(progressList);
    }

    /**
     * Get the latest progress update for an appointment.
     *
     * @param appointmentId the appointment ID
     * @return the latest progress response
     */
    @GetMapping("/{appointmentId}/latest")
    public ResponseEntity<ProgressResponse> getLatestProgress(
            @PathVariable Long appointmentId) {
        
        log.info("Fetching latest progress for appointment {}", appointmentId);
        
        List<ProgressResponse> progressList = progressService.getProgressForAppointment(appointmentId);
        
        if (progressList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ProgressResponse latest = progressList.get(progressList.size() - 1);
        
        return ResponseEntity.ok(latest);
    }

    /**
     * Get the overall progress percentage for an appointment.
     *
     * @param appointmentId the appointment ID
     * @return the progress percentage
     */
    @GetMapping("/{appointmentId}/percentage")
    public ResponseEntity<Integer> getProgressPercentage(
            @PathVariable Long appointmentId) {
        
        log.info("Calculating progress percentage for appointment {}", appointmentId);
        
        int percentage = progressService.calculateProgressPercentage(appointmentId);
        
        return ResponseEntity.ok(percentage);
    }
}
