package com.example.authservice.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.authservice.dto.response.AuthResponse.UserInfo;

@Service
public class UserServiceClient {
    
    @Value("${app.user-service.url:http://localhost:8082}")
    private String userServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public UserServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public UserInfo createUser(UUID userId, String name, String email) {
        try {
            CreateUserFromAuthRequest request = new CreateUserFromAuthRequest(userId, name, email);
            
        
            String url = userServiceUrl + "/api/users/from-auth";
        
            return new UserInfo(userId, name, email, java.time.LocalDateTime.now());
        } catch (Exception e) {
            return new UserInfo(userId, name, email, java.time.LocalDateTime.now());
        }
    }
    
    public UserInfo getUserInfo(UUID userId) {
        try {
            String url = userServiceUrl + "/api/users/" + userId;
            
            return new UserInfo(userId, "Mock User", "mock@email.com", java.time.LocalDateTime.now());
        } catch (Exception e) {
            return null;
        }
    }
    
    public static class CreateUserFromAuthRequest {
        private UUID userId;
        private String fullName;
        private String email;
        
        public CreateUserFromAuthRequest(UUID userId, String fullName, String email) {
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
        }
        
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}