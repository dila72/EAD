package com.example.ead_backend.controller;

import com.example.ead_backend.dto.ProgressResponse;
import com.example.ead_backend.dto.ProgressUpdateRequest;
import com.example.ead_backend.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for employee progress update operations.
 */
@RestController
@RequestMapping("/api/employee/progress")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000") 
public class ProgressUpdateController {

    private final ProgressService progressService;

    /**
     * Create or update progress for an appointment.
     *
     * @param appointmentId the appointment ID (UUID string)
     * @param request       the progress update request
     * @param userId        the authenticated user ID from header
     * @return the created/updated progress response
     */
    @PutMapping("/{appointmentId}")
    public ResponseEntity<ProgressResponse> updateProgress(
            @PathVariable String appointmentId,
            @Valid @RequestBody ProgressUpdateRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId) {

        log.info("Received progress update request for appointment {} from user {}", appointmentId, userId);

        ProgressResponse response = progressService.createOrUpdateProgress(appointmentId, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update status only for an appointment.
     *
     * @param appointmentId the appointment ID (UUID string)
     * @param status        the new status
     * @param userId        the authenticated user ID from header
     * @return success response
     */
    @PostMapping("/{appointmentId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable String appointmentId,
            @RequestParam String status,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId) {

        log.info("Updating status for appointment {} to {} by user {}", appointmentId, status, userId);

        ProgressUpdateRequest request = ProgressUpdateRequest.builder()
                .stage(status)
                .percentage(0) // Status update without percentage change
                .remarks("Status changed to: " + status)
                .build();

        progressService.createOrUpdateProgress(appointmentId, request, userId);

        return ResponseEntity.ok("Status updated successfully");
    }
}