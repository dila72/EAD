package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.ProgressResponse;
import com.example.ead_backend.dto.ProgressUpdateRequest;
import com.example.ead_backend.mapper.ProgressMapper;
import com.example.ead_backend.model.entity.Notification;
import com.example.ead_backend.model.entity.ProgressUpdate;
import com.example.ead_backend.model.enums.NotificationType;
import com.example.ead_backend.repository.NotificationRepository;
import com.example.ead_backend.repository.ProgressUpdateRepository;
import com.example.ead_backend.service.EmailService;
import com.example.ead_backend.service.ProgressCalculationService;
import com.example.ead_backend.service.ProgressService;
import com.example.ead_backend.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProgressService for managing progress updates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressServiceImpl implements ProgressService {

    private final ProgressUpdateRepository progressUpdateRepository;
    private final NotificationRepository notificationRepository;
    private final ProgressMapper progressMapper;
    private final ProgressCalculationService progressCalculationService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final EmailService emailService;
    private final com.example.ead_backend.repository.AppointmentRepository appointmentRepository;
    private final com.example.ead_backend.repository.ProjectRepository projectRepository;

    @Override
    @Transactional
    public ProgressResponse createOrUpdateProgress(String appointmentId, ProgressUpdateRequest request, Long updatedBy) {
        log.info("Creating/updating progress for appointment {} by user {}", appointmentId, updatedBy);

        // Convert DTO to entity
        ProgressUpdate progressUpdate = progressMapper.toEntity(request);
        progressUpdate.setAppointmentId(appointmentId);
        progressUpdate.setUpdatedBy(updatedBy);

        // Save progress update
        ProgressUpdate saved = progressUpdateRepository.save(progressUpdate);
        log.debug("Progress update saved with ID: {}", saved.getId());

        // Update appointment or project status based on progress stage
        try {
            updateAppointmentStatus(appointmentId, request.getStage(), request.getPercentage());
        } catch (Exception e) {
            log.error("Failed to update appointment/project status: {}", e.getMessage());
        }

        // Calculate overall progress percentage
        int overallProgress = progressCalculationService.getLatestProgress(appointmentId);
        log.debug("Overall progress for appointment {}: {}%", appointmentId, overallProgress);

        // Create notification
        String notificationMessage = String.format(
                "Progress updated for appointment #%s: %s (%d%%)",
                appointmentId, request.getStage(), request.getPercentage());

        Notification notification = Notification.builder()
                .userId(updatedBy) // In real scenario, should notify customer
                .type(NotificationType.PROGRESS_UPDATE)
                .message(notificationMessage)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.debug("Notification created for progress update");

        // Convert to response
        ProgressResponse response = progressMapper.toResponse(saved);

        // Broadcast via WebSocket
        try {
            webSocketNotificationService.broadcastProgressUpdate(appointmentId, response);
        } catch (Exception e) {
            log.error("Failed to broadcast progress update via WebSocket: {}", e.getMessage());
        }

        // Send email notification (async in real scenario)
        try {
            emailService.sendProgressUpdateNotification(
                    "customer@example.com", // Should fetch from appointment/customer
                    appointmentId,
                    request.getStage(),
                    request.getPercentage(),
                    request.getRemarks());
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
        }

        log.info("Progress update completed successfully for appointment {}", appointmentId);
        return response;
    }

    /**
     * Update the appointment or project status based on progress stage.
     *
     * @param id         the appointment or project ID
     * @param stage      the progress stage
     * @param percentage the progress percentage
     */
    private void updateAppointmentStatus(String id, String stage, Integer percentage) {
        String stageLower = stage != null ? stage.toLowerCase() : "";
        
        // Try to update as appointment first
        appointmentRepository.findById(id).ifPresentOrElse(appointment -> {
            com.example.ead_backend.model.enums.AppointmentStatus newStatus = null;
            
            if ("completed".equals(stageLower) || percentage >= 100) {
                newStatus = com.example.ead_backend.model.enums.AppointmentStatus.COMPLETED;
                log.info("Updating appointment {} status to COMPLETED", id);
            } else if ("cancelled".equals(stageLower)) {
                newStatus = com.example.ead_backend.model.enums.AppointmentStatus.CANCELLED;
                log.info("Updating appointment {} status to CANCELLED", id);
            } else {
                log.debug("Appointment {} progress stage is '{}' (status remains {})", id, stage, appointment.getStatus());
                return;
            }
            
            if (newStatus != null && newStatus != appointment.getStatus()) {
                appointment.setStatus(newStatus);
                appointmentRepository.save(appointment);
                log.info("Appointment {} status updated to {}", id, newStatus);
            }
        }, () -> {
            // Not an appointment, try as project
            projectRepository.findById(id).ifPresent(project -> {
                com.example.ead_backend.model.enums.ProjectStatus newStatus = null;
                
                if ("completed".equals(stageLower) || percentage >= 100) {
                    newStatus = com.example.ead_backend.model.enums.ProjectStatus.COMPLETED;
                    log.info("Updating project {} status to COMPLETED", id);
                } else if ("in progress".equals(stageLower) || "in_progress".equals(stageLower)) {
                    newStatus = com.example.ead_backend.model.enums.ProjectStatus.IN_PROGRESS;
                    log.info("Updating project {} status to IN_PROGRESS", id);
                } else if ("paused".equals(stageLower)) {
                    newStatus = com.example.ead_backend.model.enums.ProjectStatus.ON_HOLD;
                    log.info("Updating project {} status to ON_HOLD", id);
                } else if ("cancelled".equals(stageLower)) {
                    newStatus = com.example.ead_backend.model.enums.ProjectStatus.CANCELLED;
                    log.info("Updating project {} status to CANCELLED", id);
                } else if ("not started".equals(stageLower) || "not_started".equals(stageLower)) {
                    newStatus = com.example.ead_backend.model.enums.ProjectStatus.PLANNED;
                    log.info("Updating project {} status to PLANNED", id);
                }
                
                if (newStatus != null && newStatus != project.getStatus()) {
                    project.setStatus(newStatus);
                    projectRepository.save(project);
                    log.info("Project {} status updated to {}", id, newStatus);
                }
            });
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressResponse> getProgressForAppointment(String appointmentId) {
        log.debug("Fetching progress history for appointment {}", appointmentId);

        List<ProgressUpdate> updates = progressUpdateRepository.findByAppointmentIdOrderByCreatedAtAsc(appointmentId);

        return updates.stream()
                .map(progressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateProgressPercentage(String appointmentId) {
        log.debug("Calculating progress percentage for appointment {}", appointmentId);
        return progressCalculationService.getLatestProgress(appointmentId);
    }
}
