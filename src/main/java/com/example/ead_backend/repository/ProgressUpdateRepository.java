package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.ProgressUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ProgressUpdate entity operations.
 */
@Repository
public interface ProgressUpdateRepository extends JpaRepository<ProgressUpdate, Long> {

    /**
     * Find all progress updates for a specific appointment ordered by creation
     * date.
     *
     * @param appointmentId the appointment ID
     * @return list of progress updates
     */
    List<ProgressUpdate> findByAppointmentIdOrderByCreatedAtAsc(Long appointmentId);
}
