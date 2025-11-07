package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByEmailAndOtp(String email, String otp);
    Optional<OTP> findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);
}

