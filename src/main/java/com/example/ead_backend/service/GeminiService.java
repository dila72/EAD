package com.example.ead_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.List;

@Service
@Slf4j
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String generateResponse(String prompt) throws Exception {
        try {
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
                log.error("Gemini API key is not configured properly");
                throw new Exception("Gemini API key is not configured");
            }

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                    + apiKey;

            log.info("Sending request to Gemini API...");
            log.debug("Prompt length: {} characters", prompt.length());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)))));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class);

            log.info("Gemini API response status: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Gemini API returned error status: {}, body: {}",
                        response.getStatusCode(), response.getBody());
                throw new Exception("Failed to get response from Gemini API: " + response.getStatusCode());
            }

            // Parse the response to extract the generated text
            log.debug("Response body: {}", response.getBody());
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            // Check if candidates exist
            if (!jsonResponse.has("candidates") || jsonResponse.path("candidates").size() == 0) {
                log.error("No candidates in Gemini API response. Response: {}", response.getBody());
                throw new Exception("No response generated from Gemini API");
            }

            String generatedText = jsonResponse.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            log.info("Successfully received response from Gemini API, length: {} characters",
                    generatedText.length());
            return generatedText;
        } catch (Exception e) {
            log.error("Error calling Gemini API: {} - {}", e.getClass().getName(), e.getMessage(), e);
            throw new Exception("Failed to generate AI response: " + e.getMessage(), e);
        }
    }
}