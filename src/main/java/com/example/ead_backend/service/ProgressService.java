package com.example.ead_backend.service;

import com.example.ead_backend.dto.ProgressResponse;
import com.example.ead_backend.dto.ProgressUpdateRequest;

import java.util.List;

/**
 * Service interface for managing progress updates on appointments.
 */
public interface ProgressService {

    /**
     * Create or update progress for an appointment.
     *
     * @param appointmentId the appointment ID (UUID string)
     * @param request       the progress update request
     * @param updatedBy     the ID of the user making the update
     * @return the created/updated progress response
     */
    ProgressResponse createOrUpdateProgress(String appointmentId, ProgressUpdateRequest request, Long updatedBy);

    /**
     * Get all progress updates for an appointment.
     *
     * @param appointmentId the appointment ID (UUID string)
     * @return list of progress responses
     */
    List<ProgressResponse> getProgressForAppointment(String appointmentId);

    /**
     * Calculate the overall progress percentage for an appointment.
     * Uses ProgressCalculationService for computation.
     *
     * @param appointmentId the appointment ID (UUID string)
     * @return the calculated progress percentage
     */
    int calculateProgressPercentage(String appointmentId);
}
