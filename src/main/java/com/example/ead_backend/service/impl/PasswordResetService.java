package com.example.ead_backend.service.impl;

import com.example.ead_backend.model.entity.OTP;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.repository.OTPRepository;
import com.example.ead_backend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void generateAndSendOTP(String email) {
        // Check if user exists
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user found with this email address"));

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Delete any existing OTP for this email
        otpRepository.deleteByEmail(email);

        // Create new OTP entity
        OTP otpEntity = new OTP();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(10)); // Valid for 10 minutes
        otpRepository.save(otpEntity);

        // Send OTP via email
        emailService.sendOTP(email, otp);
    }

    public boolean verifyOTP(String email, String otp) {
        Optional<OTP> otpEntity = otpRepository.findByEmailAndOtp(email, otp);

        if (otpEntity.isPresent()) {
            OTP otpRecord = otpEntity.get();
            // Check if OTP is still valid (not expired)
            if (otpRecord.getExpiryTime().isAfter(LocalDateTime.now())) {
                return true;
            } else {
                // OTP expired, delete it
                otpRepository.deleteByEmail(email);
                throw new RuntimeException("OTP has expired. Please request a new one.");
            }
        }
        return false;
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        // Verify OTP
        if (!verifyOTP(email, otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Find user
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Delete used OTP
        otpRepository.deleteByEmail(email);
    }

    @Transactional
    public void deleteExpiredOTPs() {
        // This method can be called periodically to clean up expired OTPs
        otpRepository.findAll().forEach(otp -> {
            if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
                otpRepository.delete(otp);
            }
        });
    }

    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        // Find user
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Check if new password is different from old password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("New password must be different from old password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}
