package com.example.ead_backend.controller;

import com.example.ead_backend.dto.CustomerProfileDTO;
import com.example.ead_backend.dto.UpdateCustomerProfileRequest;
import com.example.ead_backend.service.CustomerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for customer profile management
 * Handles profile viewing and editing for customers
 */
@RestController
@RequestMapping("/api/customer/profile")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    /**
     * Get current authenticated customer's profile
     * Uses Spring Security Authentication to get the logged-in user
     */
    @GetMapping("/me")
    public ResponseEntity<CustomerProfileDTO> getMyProfile(Authentication authentication) {
        String email = authentication.getName(); // Gets email from authenticated user
        CustomerProfileDTO profile = customerProfileService.getCustomerProfileByEmail(email);
        return ResponseEntity.ok(profile);
    }

    /**
     * Get customer profile by user ID
     * Useful for admin or other authorized users
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomerProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        CustomerProfileDTO profile = customerProfileService.getCustomerProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Get customer profile by customer ID
     * Useful for admin or other authorized users
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerProfileDTO> getProfileByCustomerId(@PathVariable Long customerId) {
        CustomerProfileDTO profile = customerProfileService.getCustomerProfileByCustomerId(customerId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update current authenticated customer's profile
     * Email and password cannot be updated through this endpoint
     */
    @PutMapping("/me")
    public ResponseEntity<CustomerProfileDTO> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateCustomerProfileRequest request) {
        
        // Get user email from authentication
        String email = authentication.getName();
        
        // Get user profile to extract userId
        CustomerProfileDTO currentProfile = customerProfileService.getCustomerProfileByEmail(email);
        
        // Update profile
        CustomerProfileDTO updatedProfile = customerProfileService.updateCustomerProfile(
                currentProfile.getUserId(), 
                request
        );
        
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Update customer profile by user ID
     * Useful for admin or other authorized users
     */
    @PutMapping("/user/{userId}")
    public ResponseEntity<CustomerProfileDTO> updateProfileByUserId(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateCustomerProfileRequest request) {
        
        CustomerProfileDTO updatedProfile = customerProfileService.updateCustomerProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }
}
