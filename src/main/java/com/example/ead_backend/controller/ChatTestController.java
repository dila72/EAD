package com.example.ead_backend.controller;

import com.example.ead_backend.service.GeminiService;
import com.example.ead_backend.service.RAGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to debug chatbot issues
 */
@RestController
@RequestMapping("/api/chat/test")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ChatTestController {
    private final RAGService ragService;
    private final GeminiService geminiService;

    @GetMapping("/rag")
    public ResponseEntity<Map<String, Object>> testRAG(@RequestParam(defaultValue = "What services do you offer?") String question) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("Testing RAG service with question: {}", question);
            String context = ragService.buildContext(question, null);
            response.put("success", true);
            response.put("contextLength", context.length());
            response.put("context", context);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error testing RAG service", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getName());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/gemini")
    public ResponseEntity<Map<String, Object>> testGemini(@RequestParam(defaultValue = "Hello, how are you?") String prompt) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("Testing Gemini service with prompt: {}", prompt);
            String reply = geminiService.generateResponse(prompt);
            response.put("success", true);
            response.put("replyLength", reply.length());
            response.put("reply", reply);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error testing Gemini service", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getName());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/full")
    public ResponseEntity<Map<String, Object>> testFullFlow(@RequestParam(defaultValue = "What services do you offer?") String question) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("Testing full chatbot flow with question: {}", question);
            
            // Step 1: Test RAG
            String context = ragService.buildContext(question, null);
            response.put("step1_rag", "SUCCESS");
            response.put("contextLength", context.length());
            
            // Step 2: Build prompt
            String prompt = String.format("Context: %s\n\nQuestion: %s\n\nAnswer:", context.substring(0, Math.min(500, context.length())), question);
            response.put("step2_prompt", "SUCCESS");
            
            // Step 3: Call Gemini
            String reply = geminiService.generateResponse(prompt);
            response.put("step3_gemini", "SUCCESS");
            response.put("reply", reply);
            
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in full flow test", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getName());
            return ResponseEntity.ok(response);
        }
    }
}
