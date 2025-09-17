package com.example.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.dto.common.ApiResponse;
import com.example.authservice.dto.request.ForgotPasswordRequest;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.request.ResetPasswordRequest;
import com.example.authservice.dto.response.AuthResponse;
import com.example.authservice.exception.AuthException.AccountInactiveException;
import com.example.authservice.exception.AuthException.AccountLockedException;
import com.example.authservice.exception.AuthException.InvalidCredentialsException;
import com.example.authservice.exception.AuthException.TokenExpiredException;
import com.example.authservice.exception.AuthException.TokenInvalidException;
import com.example.authservice.exception.AuthException.UserAlreadyExistsException;
import com.example.authservice.exception.AuthException.UserNotFoundException;
import com.example.authservice.exception.AuthException.UserServiceException;
import com.example.authservice.exception.AuthException.ValidationException;
import com.example.authservice.service.AuthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Validated
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    // API: POST /api/auth/register - theo auth-api.md
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (UserServiceException e) {
            log.error("User service integration failed during registration: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Registration failed. Please try again"));
        } catch (Exception e) {
            log.error("Registration failed for: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Registration failed. Please try again"));
        }
    }
    
    // API: POST /api/auth/login - theo auth-api.md
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (InvalidCredentialsException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        } catch (AccountInactiveException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (AccountLockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Login failed for: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Login failed. Please try again"));
        }
    }
    
    // API: POST /api/auth/refresh - theo auth-api.md
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // VALIDATION: Extract token from Authorization header
            String refreshToken = extractTokenFromHeader(authHeader);
            
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
        } catch (TokenExpiredException | TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        } catch (AccountInactiveException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Token refresh failed. Please login again"));
        }
    }
    
    // API: POST /api/auth/logout - theo auth-api.md
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // VALIDATION: Extract token from Authorization header
            String refreshToken = extractTokenFromHeader(authHeader);
            
            authService.logout(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
        } catch (Exception e) {
            log.warn("Logout error (non-critical): {}", e.getMessage());
            // BUSINESS: Always return success for logout to avoid UX issues
            return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
        }
    }
    
    // API: POST /api/auth/reset-password/request - theo auth-api.md
    @PostMapping("/reset-password/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.requestPasswordReset(request);
            return ResponseEntity.ok(ApiResponse.success("Password reset email sent", null));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Password reset request failed for: {}", request.getEmail(), e);
            // SECURITY: Don't reveal specific errors for password reset
            return ResponseEntity.ok(ApiResponse.success("Password reset email sent", null));
        }
    }
    
    // API: POST /api/auth/reset-password - theo auth-api.md
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
        } catch (TokenExpiredException | TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Password reset failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Password reset failed. Please try again"));
        }
    }
    
    // ========== INTERNAL ENDPOINTS (OTHER SERVICES) ==========
    
    // API: POST /api/auth/validate - for internal service validation
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(
            @RequestBody TokenValidationRequest request) {
        try {
            boolean isValid = authService.validateToken(request.getToken());
            TokenValidationResponse response = new TokenValidationResponse(isValid);
            return ResponseEntity.ok(ApiResponse.success("Token validation completed", response));
        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            TokenValidationResponse response = new TokenValidationResponse(false);
            return ResponseEntity.ok(ApiResponse.success("Token validation completed", response));
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ValidationException("Authorization header", "must be in format 'Bearer <token>'");
        }
        
        String token = authHeader.substring(7);
        if (token.trim().isEmpty()) {
            throw new ValidationException("token", "cannot be empty");
        }
        
        return token;
    }
    
    // ==================== HELPER DTOs ====================
    
    public static class TokenValidationRequest {
        private String token;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
    
    public static class TokenValidationResponse {
        private boolean valid;
        
        public TokenValidationResponse(boolean valid) {
            this.valid = valid;
        }
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
    }
}