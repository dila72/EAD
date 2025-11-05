package com.example.ead_backend.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time notifications.
 * Configures STOMP protocol over WebSocket with SockJS fallback.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker for pub/sub messaging.
     *
     * @param config the message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for topic subscriptions
        config.enableSimpleBroker("/topic");

        // Set application destination prefix for client messages
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Register STOMP endpoints with SockJS fallback.
     *
     * @param registry the STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register endpoint for WebSocket connections
        registry.addEndpoint("/ws/progress")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
