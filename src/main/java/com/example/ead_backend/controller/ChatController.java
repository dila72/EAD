package com.example.ead_backend.controller;

import com.example.ead_backend.dto.ChatRequest;
import com.example.ead_backend.dto.ChatResponse;
import com.example.ead_backend.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatbotService chatbotService;

    /**
     * Main chat endpoint - accepts a question and returns an AI-generated response
     * Supports both simple Map format and structured ChatRequest format
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            // Validate request
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ChatResponse.builder()
                        .success(false)
                        .error("Message cannot be empty")
                        .timestamp(System.currentTimeMillis())
                        .build());
            }
            
            // Generate conversation ID if not provided
            String conversationId = request.getConversationId();
            if (conversationId == null || conversationId.isEmpty()) {
                conversationId = UUID.randomUUID().toString();
            }
            
            // Generate response using RAG
            String reply = chatbotService.generateResponse(
                request.getMessage(), 
                request.getCustomerId()
            );
            
            log.info("Chat request processed successfully for conversation: {}", conversationId);
            
            return ResponseEntity.ok(ChatResponse.builder()
                .reply(reply)
                .conversationId(conversationId)
                .timestamp(System.currentTimeMillis())
                .success(true)
                .build());
                
        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ChatResponse.builder()
                    .success(false)
                    .error("Unable to process your request. Please try again later.")
                    .timestamp(System.currentTimeMillis())
                    .build());
        }
    }
    
    /**
     * Health check endpoint for the chatbot service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot service is running");
    }
}
