package com.example.authservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    
    // User info according to auth-api.md
    private UserInfo user;
    private String token;      // JWT access token
    private String refreshToken;
    
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {
        private UUID id;
        private String name;
        private String email;
        private LocalDateTime createdAt;
        
        public UserInfo(UUID id, String name, String email, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.createdAt = createdAt;
        }
    }
    
    // Constructor for successful auth
    public AuthResponse(UserInfo user, String token, String refreshToken) {
        this.user = user;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}