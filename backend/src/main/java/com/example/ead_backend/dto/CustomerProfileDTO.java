package com.example.ead_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for customer profile information
 * Excludes password and email (non-editable fields)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileDTO {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email; // Read-only, not editable
    private String phoneNumber;
    private String profileImageUrl; // Local storage image URL
    private String profileImagePublicId; // File path for local storage (for deletion)
}
