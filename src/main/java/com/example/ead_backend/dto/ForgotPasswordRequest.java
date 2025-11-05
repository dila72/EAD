package com.example.ead_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgotPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

}

