package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.CustomerProfileDTO;
import com.example.ead_backend.dto.UpdateCustomerProfileRequest;
import com.example.ead_backend.model.entity.Customer;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.repository.CustomerRepository;
import com.example.ead_backend.repository.UserRepo;
import com.example.ead_backend.service.LocalFileStorageService;
import com.example.ead_backend.service.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerRepository customerRepository;
    private final UserRepo userRepository;
    private final LocalFileStorageService fileStorageService;

    @Override
    public CustomerProfileDTO getCustomerProfileByUserId(Long userId) {
        log.info("Fetching customer profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for user ID: " + userId));
        
        return mapToDTO(customer, user);
    }

    @Override
    public CustomerProfileDTO getCustomerProfileByCustomerId(Long customerId) {
        log.info("Fetching customer profile for customer ID: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        return mapToDTO(customer, customer.getUser());
    }

    @Override
    public CustomerProfileDTO getCustomerProfileByEmail(String email) {
        log.info("Fetching customer profile for email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found for email: " + email));
        
        return mapToDTO(customer, user);
    }

    @Override
    @Transactional
    public CustomerProfileDTO updateCustomerProfile(Long userId, UpdateCustomerProfileRequest request) {
        log.info("Updating customer profile for user ID: {}", userId);
        
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Fetch customer
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for user ID: " + userId));
        
        // Update user fields (firstName and lastName)
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // Update customer fields (phoneNumber)
        customer.setPhoneNumber(request.getPhoneNumber());
        
        // Save changes
        userRepository.save(user);
        customerRepository.save(customer);
        
        log.info("Customer profile updated successfully for user ID: {}", userId);
        
        return mapToDTO(customer, user);
    }

    @Override
    @Transactional
    public CustomerProfileDTO uploadProfileImage(Long userId, MultipartFile imageFile) {
        log.info("Uploading profile image for user ID: {}", userId);
        
        try {
            // Fetch customer
            Customer customer = customerRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found for user ID: " + userId));
            
            // Delete old image if exists
            if (customer.getProfileImagePublicId() != null && !customer.getProfileImagePublicId().isEmpty()) {
                log.info("Deleting old profile image: {}", customer.getProfileImagePublicId());
                fileStorageService.deleteImage(customer.getProfileImagePublicId());
            }
            
            // Upload new image to local storage with custom folder for customers
            Map<String, Object> uploadResult = fileStorageService.uploadImage(imageFile, "customers");
            
            // Extract URL and public ID from upload result
            String imageUrl = (String) uploadResult.get("secure_url");
            String imagePublicId = (String) uploadResult.get("public_id");
            
            // Update customer with new image details
            customer.setProfileImageUrl(imageUrl);
            customer.setProfileImagePublicId(imagePublicId);
            
            customerRepository.save(customer);
            
            log.info("Profile image uploaded successfully for user ID: {}", userId);
            
            return mapToDTO(customer, customer.getUser());
            
        } catch (IOException e) {
            log.error("Failed to upload profile image for user ID: {}", userId, e);
            throw new RuntimeException("Failed to upload profile image: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CustomerProfileDTO deleteProfileImage(Long userId) {
        log.info("Deleting profile image for user ID: {}", userId);
        
        try {
            // Fetch customer
            Customer customer = customerRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found for user ID: " + userId));
            
            // Delete image from local storage if exists
            if (customer.getProfileImagePublicId() != null && !customer.getProfileImagePublicId().isEmpty()) {
                fileStorageService.deleteImage(customer.getProfileImagePublicId());
            }
            
            // Clear image fields
            customer.setProfileImageUrl(null);
            customer.setProfileImagePublicId(null);
            
            customerRepository.save(customer);
            
            log.info("Profile image deleted successfully for user ID: {}", userId);
            
            return mapToDTO(customer, customer.getUser());
            
        } catch (IOException e) {
            log.error("Failed to delete profile image for user ID: {}", userId, e);
            throw new RuntimeException("Failed to delete profile image: " + e.getMessage());
        }
    }

    /**
     * Helper method to map Customer and User entities to CustomerProfileDTO
     */
    private CustomerProfileDTO mapToDTO(Customer customer, User user) {
        return CustomerProfileDTO.builder()
                .id(customer.getId())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .profileImageUrl(customer.getProfileImageUrl())
                .profileImagePublicId(customer.getProfileImagePublicId())
                .build();
    }
}
