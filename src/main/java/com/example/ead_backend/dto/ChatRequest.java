package com.example.ead_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String message;
    private String customerId; // Optional: for personalized responses
    private String conversationId; // Optional: for tracking conversation history
}
