package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Notification entity operations.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a specific user ordered by creation date descending.
     *
     * @param userId the user ID
     * @return list of notifications
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
