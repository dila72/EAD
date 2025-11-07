package com.example.ead_backend.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {
    private final RAGService ragService;
    private final GeminiService geminiService;

    /**
     * Generate a response using RAG (Retrieval-Augmented Generation)
     * 
     * @param question   The user's question
     * @param customerId Optional customer ID for personalized responses
     * @return The chatbot's response
     */
    public String generateResponse(String question, String customerId) throws Exception {
        try {
            log.info("Generating response for question: '{}', customerId: '{}'", question, customerId);

            // Retrieve relevant context using RAG
            log.info("Building context from RAG service...");
            String context = ragService.buildContext(question, customerId);
            log.info("Context retrieved successfully, length: {}", context.length());

            // Build the prompt with system instructions and context
            log.info("Building prompt...");
            String prompt = buildPrompt(context, question);
            log.debug("Prompt built successfully, length: {}", prompt.length());

            // Generate response using Gemini AI
            log.info("Calling Gemini AI service...");
            String response = geminiService.generateResponse(prompt);

            log.info("Generated response successfully for question: '{}', response length: {}",
                    question, response != null ? response.length() : 0);
            return response;
        } catch (Exception e) {
            log.error("Error generating chatbot response for question: '{}', customerId: '{}' - Error: {} - {}",
                    question, customerId, e.getClass().getName(), e.getMessage(), e);
            throw new Exception("Unable to generate response at this time. Please try again later.", e);
        }
    }

    /**
     * Overloaded method for backward compatibility
     */
    public String generateResponse(String question) throws Exception {
        return generateResponse(question, null);
    }

    private String buildPrompt(String context, String question) {
        return String.format("""
                You are a helpful AI assistant for EAD Automobile Service Center. Your role is to assist customers with:
                - Information about available services and their prices
                - Available appointment slots and booking information
                - Their existing appointments and status
                - General inquiries about the service center

                IMPORTANT INSTRUCTIONS:
                1. Use ONLY the information provided in the context below to answer questions
                2. If the context doesn't contain relevant information, politely say you don't have that information
                3. Be friendly, professional, and concise
                4. If asked to book an appointment, guide them to use the booking system
                5. Format prices with $ symbol
                6. Format dates and times clearly
                7. Don't make up information - stick to the facts in the context

                CONTEXT:
                %s

                CUSTOMER QUESTION: %s

                YOUR RESPONSE (be helpful and accurate):
                """, context, question);
    }
}