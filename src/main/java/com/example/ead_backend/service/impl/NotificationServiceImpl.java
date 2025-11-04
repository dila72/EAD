package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.NotificationDTO;
import com.example.ead_backend.mapper.NotificationMapper;
import com.example.ead_backend.model.entity.Notification;
import com.example.ead_backend.model.enums.NotificationType;
import com.example.ead_backend.repository.NotificationRepository;
import com.example.ead_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of NotificationService for managing notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationDTO createNotification(Long userId, NotificationType type, String message) {
        log.info("Creating notification for user {} of type {}", userId, type);

        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.debug("Notification created with ID: {}", saved.getId());

        return notificationMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        log.debug("Fetching notifications for user {}", userId);
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        log.debug("Marking notification {} as read", notificationId);
        
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            log.info("Notification {} marked as read", notificationId);
        });
    }
}
