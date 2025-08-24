package com.example.authservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.authservice.enums.AuthProvider;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // seconds
    
    // User info from User Service
    private UUID userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String role;
    
    // Auth info
    private AuthProvider provider;
    private LocalDateTime lastLogin;
    
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
    }
}