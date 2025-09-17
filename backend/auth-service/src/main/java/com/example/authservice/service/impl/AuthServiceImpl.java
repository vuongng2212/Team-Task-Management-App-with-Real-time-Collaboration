package com.example.authservice.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authservice.dto.request.ForgotPasswordRequest;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.request.ResetPasswordRequest;
import com.example.authservice.dto.response.AuthResponse;
import com.example.authservice.dto.response.AuthResponse.UserInfo;
import com.example.authservice.entity.AuthUser;
import com.example.authservice.entity.PasswordResetToken;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.exception.AuthException.*;
import com.example.authservice.exception.AuthException.AccountInactiveException;
import com.example.authservice.exception.AuthException.AccountLockedException;
import com.example.authservice.exception.AuthException.InvalidCredentialsException;
import com.example.authservice.exception.AuthException.TokenExpiredException;
import com.example.authservice.exception.AuthException.TokenInvalidException;
import com.example.authservice.exception.AuthException.UserAlreadyExistsException;
import com.example.authservice.exception.AuthException.UserNotFoundException;
import com.example.authservice.exception.AuthException.UserServiceException;
import com.example.authservice.exception.AuthException.ValidationException;
import com.example.authservice.repository.AuthUserRepository;
import com.example.authservice.repository.PasswordResetTokenRepository;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.PasswordService;
import com.example.authservice.service.UserServiceClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private AuthUserRepository authUserRepository;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordService passwordService;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    // PERFORMANCE: Simple caching for frequently accessed auth data
    private final ConcurrentHashMap<String, AuthUser> authUserCache = new ConcurrentHashMap<>();
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("User registration attempt for email: {}", request.getEmail());
        
        // VALIDATION: Business rules validation
        validateRegistrationRequest(request);
        
        // BUSINESS: Check if user already exists
        if (authUserRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - user already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException(request.getEmail());
        }
        
        // BUSINESS: Create auth user
        AuthUser authUser = new AuthUser();
        authUser.setId(UUID.randomUUID());
        authUser.setEmail(request.getEmail());
        authUser.setPasswordHash(passwordService.hashPassword(request.getPassword()));
        authUser.setIsActive(true);
        
        AuthUser savedAuthUser = authUserRepository.save(authUser);
        
        // INTEGRATION: Create user profile in user-service
        UserInfo userInfo;
        try {
            userInfo = userServiceClient.createUser(
                savedAuthUser.getId(),
                request.getName(),
                request.getEmail()
            );
        } catch (Exception e) {
            log.error("Failed to create user profile for: {}", request.getEmail(), e);
            // ROLLBACK: Delete auth user if profile creation fails
            authUserRepository.delete(savedAuthUser);
            throw new UserServiceException("create user profile", e.getMessage());
        }
        
        // SECURITY: Generate tokens
        String accessToken = jwtService.generateAccessToken(
            savedAuthUser.getId(),
            savedAuthUser.getEmail(),
            request.getName()
        );
        String refreshToken = jwtService.generateRefreshToken(savedAuthUser.getId());
        
        // SECURITY: Store refresh token
        storeRefreshToken(savedAuthUser, refreshToken);
        
        // PERFORMANCE: Cache the auth user
        authUserCache.put(savedAuthUser.getEmail(), savedAuthUser);
        
        log.info("User registered successfully: {}", request.getEmail());
        return new AuthResponse(userInfo, accessToken, refreshToken);
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        // VALIDATION: Basic validation
        validateLoginRequest(request);
        
        // PERFORMANCE: Try cache first
        AuthUser authUser = authUserCache.get(request.getEmail());
        if (authUser == null) {
            // FALLBACK: Database lookup
            authUser = authUserRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", request.getEmail());
                    return new InvalidCredentialsException();
                });
            
            // PERFORMANCE: Cache for future use
            authUserCache.put(request.getEmail(), authUser);
        }
        
        // BUSINESS: Check account status
        if (!authUser.getIsActive()) {
            log.warn("Login attempt on inactive account: {}", request.getEmail());
            throw new AccountInactiveException();
        }
        
        // SECURITY: Check if account is locked
        if (authUser.isLocked()) {
            log.warn("Login attempt on locked account: {}", request.getEmail());
            throw new AccountLockedException(authUser.getRemainingLockoutMinutes());
        }
        
        // SECURITY: Verify password
        if (!passwordService.verifyPassword(request.getPassword(), authUser.getPasswordHash())) {
            log.warn("Invalid password attempt for: {}", request.getEmail());
            
            // SECURITY: Record failed attempt
            authUser.recordFailedAttempt();
            authUserRepository.save(authUser);
            authUserCache.put(request.getEmail(), authUser); // Update cache
            
            throw new InvalidCredentialsException();
        }
        
        // INTEGRATION: Get user info from user-service
        UserInfo userInfo = userServiceClient.getUserInfo(authUser.getId());
        if (userInfo == null) {
            log.warn("User profile not found for auth user: {}", authUser.getId());
            // FALLBACK: Create basic user info
            userInfo = new UserInfo(authUser.getId(), "User", authUser.getEmail(), authUser.getCreatedAt());
        }
        
        // SECURITY: Generate new tokens
        String accessToken = jwtService.generateAccessToken(
            authUser.getId(),
            authUser.getEmail(),
            userInfo.getName()
        );
        String refreshToken = jwtService.generateRefreshToken(authUser.getId());
        
        // SECURITY: Store refresh token
        storeRefreshToken(authUser, refreshToken);
        
        // BUSINESS: Reset failed attempts on successful login
        authUser.resetFailedAttempts();
        authUserRepository.save(authUser);
        authUserCache.put(request.getEmail(), authUser); // Update cache
        
        log.info("User logged in successfully: {} (Status: {})", request.getEmail(), authUser.getAccountStatus());
        return new AuthResponse(userInfo, accessToken, refreshToken);
    }
    
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.debug("Refresh token request");
        
        // SECURITY: Validate token format
        if (!jwtService.validateToken(refreshToken)) {
            throw new TokenInvalidException("Invalid token format");
        }
        
        // SECURITY: Get user ID from token
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        
        // SECURITY: Verify stored token
        String tokenHash = jwtService.hashToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new TokenInvalidException("Token not found or revoked"));
        
        // SECURITY: Check token expiration
        if (storedToken.isExpired()) {
            log.warn("Expired refresh token used: {}", userId);
            refreshTokenRepository.delete(storedToken);
            throw new TokenExpiredException("Refresh");
        }
        
        // BUSINESS: Get auth user
        AuthUser authUser = authUserRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User ID: " + userId));
        
        // BUSINESS: Check if user is still active
        if (!authUser.getIsActive()) {
            log.warn("Refresh token used for inactive user: {}", userId);
            throw new AccountInactiveException();
        }
        
        // INTEGRATION: Get user info
        UserInfo userInfo = userServiceClient.getUserInfo(userId);
        if (userInfo == null) {
            userInfo = new UserInfo(authUser.getId(), "User", authUser.getEmail(), authUser.getCreatedAt());
        }
        
        // SECURITY: Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(
            authUser.getId(),
            authUser.getEmail(),
            userInfo.getName()
        );
        String newRefreshToken = jwtService.generateRefreshToken(authUser.getId());
        
        // SECURITY: Replace old refresh token
        refreshTokenRepository.delete(storedToken);
        storeRefreshToken(authUser, newRefreshToken);
        
        log.debug("Tokens refreshed successfully for user: {}", userId);
        return new AuthResponse(userInfo, newAccessToken, newRefreshToken);
    }
    
    @Override
    public void logout(String refreshToken) {
        log.debug("Logout request");
        
        try {
            String tokenHash = jwtService.hashToken(refreshToken);
            RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElse(null);
            
            if (storedToken != null) {
                refreshTokenRepository.delete(storedToken);
                log.debug("Refresh token invalidated for user: {}", storedToken.getUser().getId());
            }
        } catch (Exception e) {
            log.warn("Error during logout: {}", e.getMessage());
            // DON'T FAIL: Logout should always succeed from user perspective
        }
    }
    
    @Override
    public void requestPasswordReset(ForgotPasswordRequest request) {
        log.info("Password reset requested for: {}", request.getEmail());
        
        // VALIDATION: Basic email validation
        validateEmailFormat(request.getEmail());
        
        // BUSINESS: Find user (don't reveal if user exists for security)
        AuthUser authUser = authUserRepository.findByEmailAndIsActiveTrue(request.getEmail())
            .orElse(null);
        
        if (authUser == null) {
            log.warn("Password reset requested for non-existent user: {}", request.getEmail());
            // SECURITY: Don't reveal user doesn't exist - just return success
            return;
        }
        
        // CLEANUP: Remove any existing reset tokens
        passwordResetTokenRepository.deleteByUserId(authUser.getId());
        
        // SECURITY: Generate reset token (valid for 1 hour)
        String resetToken = UUID.randomUUID().toString();
        String tokenHash = jwtService.hashToken(resetToken);
        
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(authUser);
        passwordResetToken.setTokenHash(tokenHash);
        passwordResetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        
        passwordResetTokenRepository.save(passwordResetToken);
        
        // TODO: NOTIFICATION-001 - Send email when notification service is ready
        // For now, just log the token (DEVELOPMENT ONLY)
        log.info("Password reset token for {}: {}", request.getEmail(), resetToken);
    }
    
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Password reset attempt");
        
        // VALIDATION: Validate new password strength
        validatePasswordStrength(request.getNewPassword());
        
        // SECURITY: Find and validate reset token
        String tokenHash = jwtService.hashToken(request.getToken());
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new TokenInvalidException("Invalid reset token"));
        
        // SECURITY: Check token expiration
        if (resetToken.isExpired()) {
            log.warn("Expired reset token used");
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Password reset");
        }
        
        // BUSINESS: Update password
        AuthUser authUser = resetToken.getUser();
        authUser.setPasswordHash(passwordService.hashPassword(request.getNewPassword()));
        authUser.resetFailedAttempts(); // Reset any failed attempts
        authUserRepository.save(authUser);
        
        // CLEANUP: Delete reset token
        passwordResetTokenRepository.delete(resetToken);
        
        // SECURITY: Invalidate all refresh tokens for this user
        refreshTokenRepository.deleteByUserId(authUser.getId());
        
        // PERFORMANCE: Clear cache
        authUserCache.remove(authUser.getEmail());
        
        log.info("Password reset successful for user: {}", authUser.getId());
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
    
    @Override
    public void deactivateUser(String email) {
        log.info("Deactivating user: {}", email);
        
        AuthUser authUser = authUserRepository.findByEmailAndIsActiveTrue(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        
        authUser.setIsActive(false);
        authUserRepository.save(authUser);
        
        // CLEANUP: Invalidate all tokens
        refreshTokenRepository.deleteByUserId(authUser.getId());
        
        // PERFORMANCE: Clear cache
        authUserCache.remove(email);
        
        log.info("User deactivated: {}", email);
    }
    
    @Override
    public void reactivateUser(String email) {
        log.info("Reactivating user: {}", email);
        
        AuthUser authUser = authUserRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        
        authUser.setIsActive(true);
        authUser.resetFailedAttempts(); // Reset any lockouts
        authUserRepository.save(authUser);
        
        log.info("User reactivated: {}", email);
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    private void validateRegistrationRequest(RegisterRequest request) {
        // VALIDATION: Email format
        validateEmailFormat(request.getEmail());
        
        // VALIDATION: Name requirements
        if (request.getName() == null || request.getName().trim().length() < 2) {
            throw new ValidationException("name", "must be at least 2 characters long");
        }
        
        if (request.getName().length() > 100) {
            throw new ValidationException("name", "cannot exceed 100 characters");
        }
        
        // VALIDATION: Password strength
        validatePasswordStrength(request.getPassword());
    }
    
    private void validateLoginRequest(LoginRequest request) {
        // VALIDATION: Email format
        validateEmailFormat(request.getEmail());
        
        // VALIDATION: Password not empty
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("password", "is required");
        }
    }
    
    private void validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", "is required");
        }
        
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new ValidationException("email", "format is invalid");
        }
        
        if (email.length() > 254) {
            throw new ValidationException("email", "is too long (max 254 characters)");
        }
    }
    
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("password", "must be at least 8 characters long");
        }
        
        if (password.length() > 128) {
            throw new ValidationException("password", "cannot exceed 128 characters");
        }
        
        // SECURITY: Password complexity requirements
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("password", "must contain at least one lowercase letter");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("password", "must contain at least one uppercase letter");
        }
        
        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("password", "must contain at least one number");
        }
        
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new ValidationException("password", "must contain at least one special character");
        }
    }
    
    private void storeRefreshToken(AuthUser authUser, String refreshToken) {
        String tokenHash = jwtService.hashToken(refreshToken);
        
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(authUser);
        refreshTokenEntity.setTokenHash(tokenHash);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiration()));
        
        refreshTokenRepository.save(refreshTokenEntity);
    }
}