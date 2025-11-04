package com.example.ead_backend.service;

import com.example.ead_backend.model.entity.ProgressUpdate;
import com.example.ead_backend.repository.ProgressUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for calculating progress percentages based on recorded updates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressCalculationService {

    private final ProgressUpdateRepository progressUpdateRepository;

    /**
     * Calculate the average progress percentage for an appointment.
     * Uses the average of all recorded percentage entries.
     *
     * @param appointmentId the appointment ID
     * @return the calculated average percentage
     */
    public int calculateAverageProgress(Long appointmentId) {
        List<ProgressUpdate> updates = progressUpdateRepository.findByAppointmentIdOrderByCreatedAtAsc(appointmentId);

        if (updates.isEmpty()) {
            log.debug("No progress updates found for appointment {}", appointmentId);
            return 0;
        }

        int sum = updates.stream()
                .mapToInt(ProgressUpdate::getPercentage)
                .sum();

        int average = sum / updates.size();
        log.debug("Calculated average progress for appointment {}: {}%", appointmentId, average);

        return average;
    }

    /**
     * Get the latest progress percentage for an appointment.
     *
     * @param appointmentId the appointment ID
     * @return the latest percentage or 0 if no updates exist
     */
    public int getLatestProgress(Long appointmentId) {
        List<ProgressUpdate> updates = progressUpdateRepository.findByAppointmentIdOrderByCreatedAtAsc(appointmentId);

        if (updates.isEmpty()) {
            return 0;
        }

        return updates.get(updates.size() - 1).getPercentage();
    }
}
