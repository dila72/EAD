package com.example.ead_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@automobile.com}")
    private String fromAddress;

    /**
     * Send a progress update email to a user.
     *
     * @param toEmail the recipient email address
     * @param subject the email subject
     * @param body    the email body
     */
    public void sendProgressEmail(String toEmail, String subject, String body) {
        try {
            log.info("Sending progress email to {}", toEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            // Don't throw exception - email failure shouldn't break the flow
        }
    }

    /**
     * Send a progress update email using appointment details.
     *
     * @param toEmail       the recipient email
     * @param appointmentId the appointment ID
     * @param stage         the current stage
     * @param percentage    the progress percentage
     * @param remarks       additional remarks
     */
    public void sendProgressUpdateNotification(String toEmail, Long appointmentId, String stage, Integer percentage, String remarks) {
        String subject = "Progress Update - Appointment #" + appointmentId;
        String body = String.format(
                "Dear Customer,\n\n" +
                "Your appointment #%d has been updated:\n\n" +
                "Stage: %s\n" +
                "Progress: %d%%\n" +
                "Remarks: %s\n\n" +
                "Thank you for choosing our service.\n\n" +
                "Best regards,\n" +
                "Automobile Service Team",
                appointmentId, stage, percentage, remarks != null ? remarks : "N/A"
        );

        sendProgressEmail(toEmail, subject, body);
    }
}
