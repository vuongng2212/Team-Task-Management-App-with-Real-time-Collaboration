package com.example.authservice.service;

import com.example.authservice.dto.request.ForgotPasswordRequest;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.request.ResetPasswordRequest;
import com.example.authservice.dto.response.AuthResponse;

public interface AuthService {
    
    /**
     * Register a new user
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Login user
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * Refresh access token using refresh token
     */
    AuthResponse refreshToken(String refreshToken);
    
    /**
     * Logout user - invalidate refresh token
     */
    void logout(String refreshToken);
    
    /**
     * Send password reset email
     */
    void requestPasswordReset(ForgotPasswordRequest request);
    
    /**
     * Reset password using token
     */
    void resetPassword(ResetPasswordRequest request);
    
    /**
     * Validate JWT token
     */
    boolean validateToken(String token);
} 