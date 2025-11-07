package com.example.ead_backend.service;

import com.example.ead_backend.dto.ProgressResponse;
import com.example.ead_backend.dto.ProgressUpdateRequest;
import com.example.ead_backend.mapper.ProgressMapper;
import com.example.ead_backend.model.entity.Notification;
import com.example.ead_backend.model.entity.ProgressUpdate;
import com.example.ead_backend.model.enums.NotificationType;
import com.example.ead_backend.repository.NotificationRepository;
import com.example.ead_backend.repository.ProgressUpdateRepository;
import com.example.ead_backend.service.impl.ProgressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProgressService implementation.
 */
@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ProgressUpdateRepository progressUpdateRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ProgressMapper progressMapper;

    @Mock
    private ProgressCalculationService progressCalculationService;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private ProgressUpdateRequest testRequest;
    private ProgressUpdate testProgressUpdate;
    private ProgressResponse testProgressResponse;

    @BeforeEach
    void setUp() {
        testRequest = ProgressUpdateRequest.builder()
                .stage("Inspection")
                .percentage(50)
                .remarks("Initial inspection completed")
                .build();

        testProgressUpdate = ProgressUpdate.builder()
                .id(1L)
                .appointmentId("100")
                .stage("Inspection")
                .percentage(50)
                .remarks("Initial inspection completed")
                .updatedBy(10L)
                .createdAt(Timestamp.from(Instant.now()))
                .updatedAt(Timestamp.from(Instant.now()))
                .build();

        testProgressResponse = ProgressResponse.builder()
                .id(1L)
                .appointmentId("100")
                .stage("Inspection")
                .percentage(50)
                .remarks("Initial inspection completed")
                .updatedBy(10L)
                .updatedAt(Timestamp.from(Instant.now()))
                .build();
    }

    @Test
    void testCreateOrUpdateProgress_Success() {
        // Arrange
        String appointmentId = "100";
        Long updatedBy = 10L;

        when(progressMapper.toEntity(testRequest)).thenReturn(testProgressUpdate);
        when(progressUpdateRepository.save(any(ProgressUpdate.class))).thenReturn(testProgressUpdate);
        when(progressCalculationService.getLatestProgress(appointmentId)).thenReturn(50);
        when(progressMapper.toResponse(testProgressUpdate)).thenReturn(testProgressResponse);
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        // Act
        ProgressResponse result = progressService.createOrUpdateProgress(appointmentId, testRequest, updatedBy);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAppointmentId()).isEqualTo(appointmentId);
        assertThat(result.getStage()).isEqualTo("Inspection");
        assertThat(result.getPercentage()).isEqualTo(50);

        // Verify interactions
        verify(progressUpdateRepository).save(any(ProgressUpdate.class));
        verify(notificationRepository).save(any(Notification.class));
        verify(webSocketNotificationService).broadcastProgressUpdate(anyString(), any(ProgressResponse.class));
        verify(emailService).sendProgressUpdateNotification(anyString(), anyString(), anyString(), anyInt(), anyString());
    }

    @Test
    void testCreateOrUpdateProgress_CreatesNotification() {
        // Arrange
        String appointmentId = "100";
        Long updatedBy = 10L;

        when(progressMapper.toEntity(testRequest)).thenReturn(testProgressUpdate);
        when(progressUpdateRepository.save(any(ProgressUpdate.class))).thenReturn(testProgressUpdate);
        when(progressCalculationService.getLatestProgress(appointmentId)).thenReturn(50);
        when(progressMapper.toResponse(testProgressUpdate)).thenReturn(testProgressResponse);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        when(notificationRepository.save(notificationCaptor.capture())).thenReturn(new Notification());

        // Act
        progressService.createOrUpdateProgress(appointmentId, testRequest, updatedBy);

        // Assert
        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification).isNotNull();
        assertThat(savedNotification.getType()).isEqualTo(NotificationType.PROGRESS_UPDATE);
        assertThat(savedNotification.getIsRead()).isFalse();
        assertThat(savedNotification.getMessage()).contains("Progress updated");
    }

    @Test
    void testGetProgressForAppointment_ReturnsMultipleUpdates() {
        // Arrange
        String appointmentId = "100";

        ProgressUpdate update1 = ProgressUpdate.builder()
                .id(1L)
                .appointmentId(appointmentId)
                .stage("Inspection")
                .percentage(25)
                .build();

        ProgressUpdate update2 = ProgressUpdate.builder()
                .id(2L)
                .appointmentId(appointmentId)
                .stage("Repair")
                .percentage(75)
                .build();

        List<ProgressUpdate> updates = Arrays.asList(update1, update2);

        when(progressUpdateRepository.findByAppointmentIdOrderByCreatedAtAsc(appointmentId)).thenReturn(updates);
        when(progressMapper.toResponse(update1)).thenReturn(ProgressResponse.builder().id(1L).stage("Inspection").percentage(25).build());
        when(progressMapper.toResponse(update2)).thenReturn(ProgressResponse.builder().id(2L).stage("Repair").percentage(75).build());

        // Act
        List<ProgressResponse> results = progressService.getProgressForAppointment(appointmentId);

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getStage()).isEqualTo("Inspection");
        assertThat(results.get(1).getStage()).isEqualTo("Repair");
    }

    @Test
    void testCalculateProgressPercentage_DelegatesToCalculationService() {
        // Arrange
        String appointmentId = "100";
        when(progressCalculationService.getLatestProgress(appointmentId)).thenReturn(65);

        // Act
        int result = progressService.calculateProgressPercentage(appointmentId);

        // Assert
        assertThat(result).isEqualTo(65);
        verify(progressCalculationService).getLatestProgress(appointmentId);
    }

    @Test
    void testCreateOrUpdateProgress_HandlesWebSocketFailure() {
        // Arrange
        String appointmentId = "100";
        Long updatedBy = 10L;

        when(progressMapper.toEntity(testRequest)).thenReturn(testProgressUpdate);
        when(progressUpdateRepository.save(any(ProgressUpdate.class))).thenReturn(testProgressUpdate);
        when(progressCalculationService.getLatestProgress(appointmentId)).thenReturn(50);
        when(progressMapper.toResponse(testProgressUpdate)).thenReturn(testProgressResponse);
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        // Simulate WebSocket failure
        doThrow(new RuntimeException("WebSocket connection failed"))
                .when(webSocketNotificationService).broadcastProgressUpdate(anyString(), any(ProgressResponse.class));

        // Act & Assert - should not throw exception
        ProgressResponse result = progressService.createOrUpdateProgress(appointmentId, testRequest, updatedBy);

        // Verify the operation completes despite WebSocket failure
        assertThat(result).isNotNull();
        verify(progressUpdateRepository).save(any(ProgressUpdate.class));
    }

    @Test
    void testCreateOrUpdateProgress_HandlesEmailFailure() {
        // Arrange
        String appointmentId = "100";
        Long updatedBy = 10L;

        when(progressMapper.toEntity(testRequest)).thenReturn(testProgressUpdate);
        when(progressUpdateRepository.save(any(ProgressUpdate.class))).thenReturn(testProgressUpdate);
        when(progressCalculationService.getLatestProgress(appointmentId)).thenReturn(50);
        when(progressMapper.toResponse(testProgressUpdate)).thenReturn(testProgressResponse);
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        // Simulate email failure
        doThrow(new RuntimeException("Email server unavailable"))
                .when(emailService).sendProgressUpdateNotification(anyString(), anyString(), anyString(), anyInt(), anyString());

        // Act & Assert - should not throw exception
        ProgressResponse result = progressService.createOrUpdateProgress(appointmentId, testRequest, updatedBy);

        // Verify the operation completes despite email failure
        assertThat(result).isNotNull();
        verify(progressUpdateRepository).save(any(ProgressUpdate.class));
    }
}
