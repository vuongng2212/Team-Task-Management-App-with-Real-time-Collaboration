package com.example.authservice.service;

import java.util.UUID;

public interface JwtService {
    
    String generateAccessToken(UUID userId, String email, String name);
    String generateRefreshToken(UUID userId);
    
    boolean validateToken(String token);
    UUID getUserIdFromToken(String token);
    String getEmailFromToken(String token);
    String getNameFromToken(String token);
    
    long getAccessTokenExpiration();
    long getRefreshTokenExpiration();
    
    String hashToken(String token);
}
