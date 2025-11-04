package com.example.ead_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOTP(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Password Reset OTP - Auto Mobile");
            message.setText("Dear User,\n\n" +
                    "You have requested to reset your password.\n\n" +
                    "Your OTP for password reset is: " + otp + "\n\n" +
                    "This OTP is valid for 10 minutes.\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Auto Mobile Team");

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}

