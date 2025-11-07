package com.example.ead_backend.service;

import com.example.ead_backend.dto.CustomerProfileDTO;
import com.example.ead_backend.dto.UpdateCustomerProfileRequest;

public interface CustomerProfileService {
    
    /**
     * Get customer profile by user ID
     * @param userId the user ID
     * @return CustomerProfileDTO
     */
    CustomerProfileDTO getCustomerProfileByUserId(Long userId);
    
    /**
     * Get customer profile by customer ID
     * @param customerId the customer ID
     * @return CustomerProfileDTO
     */
    CustomerProfileDTO getCustomerProfileByCustomerId(Long customerId);
    
    /**
     * Get customer profile by email
     * @param email the user email
     * @return CustomerProfileDTO
     */
    CustomerProfileDTO getCustomerProfileByEmail(String email);
    
    /**
     * Update customer profile
     * @param userId the user ID
     * @param request the update request
     * @return updated CustomerProfileDTO
     */
    CustomerProfileDTO updateCustomerProfile(Long userId, UpdateCustomerProfileRequest request);
}
