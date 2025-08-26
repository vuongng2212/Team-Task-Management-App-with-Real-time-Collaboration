package com.example.authservice.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.authservice.dto.response.AuthResponse.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceClient {
    
    @Value("${app.user-service.url:http://localhost:8082}")
    private String userServiceUrl;
    
    private final WebClient webClient;
    
    public UserServiceClient(WebClient.Builder webClientBuilder, @Value("${app.user-service.url:http://localhost:8082}") String userServiceUrl) {
        this.webClient = webClientBuilder
            .baseUrl(userServiceUrl)
            .build();
    }
    
    public UserInfo createUser(UUID userId, String name, String email) {
        try {
            CreateUserFromAuthRequest request = new CreateUserFromAuthRequest(userId, name, email);
            
            UserResponse response = webClient.post()
                .uri("/api/users/from-auth")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            
            if (response != null) {
                return new UserInfo(response.getId(), response.getName(), response.getEmail(), response.getCreatedAt());
            }
            
            // Fallback
            return new UserInfo(userId, name, email, LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("Failed to create user in user-service: {}", e.getMessage());
            // Return fallback UserInfo
            return new UserInfo(userId, name, email, LocalDateTime.now());
        }
    }
    
    public UserInfo getUserInfo(UUID userId) {
        try {
            UserResponse response = webClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            
            if (response != null) {
                return new UserInfo(response.getId(), response.getName(), response.getEmail(), response.getCreatedAt());
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("Failed to get user info from user-service: {}", e.getMessage());
            return null;
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserFromAuthRequest {
        private UUID userId;
        private String name;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private UUID id;
        private String name;
        private String email;
        private String avatar;
        private String bio;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}