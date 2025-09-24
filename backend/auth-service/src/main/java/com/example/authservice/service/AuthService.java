package com.example.authservice.service;

import com.example.authservice.dto.request.ForgotPasswordRequest;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.request.ResetPasswordRequest;
import com.example.authservice.dto.response.AuthResponse;

/**
 * Core authentication service contract
 * 
 * DESIGN: Clear separation of concerns
 * BUSINESS: All auth operations through this interface
 */
public interface AuthService {
    
    // CORE: Authentication operations
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
    
    // RECOVERY: Password reset flow
    void requestPasswordReset(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    
    // VALIDATION: Token validation for other services
    boolean validateToken(String token);
    
    // ADMIN: User management
    void deactivateUser(String email);
    void reactivateUser(String email);
} 