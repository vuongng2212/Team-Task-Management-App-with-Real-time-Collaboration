package com.example.authservice.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

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
import com.example.authservice.repository.AuthUserRepository;
import com.example.authservice.repository.PasswordResetTokenRepository;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.PasswordService;
import com.example.authservice.service.UserServiceClient;

@Service
@Transactional
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
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        
        AuthUser authUser = new AuthUser();
        authUser.setId(UUID.randomUUID());
        authUser.setEmail(request.getEmail());
        authUser.setPasswordHash(passwordService.hashPassword(request.getPassword()));
        authUser.setIsActive(true);
        
        AuthUser savedAuthUser = authUserRepository.save(authUser);
        
        UserInfo userInfo = userServiceClient.createUser(
            savedAuthUser.getId(),
            request.getName(),
            request.getEmail()
        );
        
        String accessToken = jwtService.generateAccessToken(
            savedAuthUser.getId(),
            savedAuthUser.getEmail(),
            request.getName()
        );
        String refreshToken = jwtService.generateRefreshToken(savedAuthUser.getId());
        
        storeRefreshToken(savedAuthUser, refreshToken);
        
        return new AuthResponse(userInfo, accessToken, refreshToken);
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        AuthUser authUser = authUserRepository.findByEmailAndIsActiveTrue(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordService.verifyPassword(request.getPassword(), authUser.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        UserInfo userInfo = userServiceClient.getUserInfo(authUser.getId());
        if (userInfo == null) {
            userInfo = new UserInfo(authUser.getId(), "User", authUser.getEmail(), authUser.getCreatedAt());
        }

        String accessToken = jwtService.generateAccessToken(
            authUser.getId(),
            authUser.getEmail(),
            userInfo.getName()
        );
        String refreshToken = jwtService.generateRefreshToken(authUser.getId());
        
        // Store refresh token
        storeRefreshToken(authUser, refreshToken);
        
        return new AuthResponse(userInfo, accessToken, refreshToken);
    }
    
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        
        String tokenHash = jwtService.hashToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("Refresh token expired");
        }
        
        AuthUser authUser = authUserRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserInfo userInfo = userServiceClient.getUserInfo(userId);
        if (userInfo == null) {
            userInfo = new UserInfo(authUser.getId(), "User", authUser.getEmail(), authUser.getCreatedAt());
        }
        
        String newAccessToken = jwtService.generateAccessToken(
            authUser.getId(),
            authUser.getEmail(),
            userInfo.getName()
        );
        String newRefreshToken = jwtService.generateRefreshToken(authUser.getId());
        
        refreshTokenRepository.delete(storedToken);
        storeRefreshToken(authUser, newRefreshToken);
        
        return new AuthResponse(userInfo, newAccessToken, newRefreshToken);
    }
    
    @Override
    public void logout(String refreshToken) {
        try {
            String tokenHash = jwtService.hashToken(refreshToken);
            RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElse(null);
            
            if (storedToken != null) {
                refreshTokenRepository.delete(storedToken);
            }
        } catch (Exception e) {
        }
    }
    
    @Override
    public void requestPasswordReset(ForgotPasswordRequest request) {
        AuthUser authUser = authUserRepository.findByEmailAndIsActiveTrue(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User with this email not found"));
        
        passwordResetTokenRepository.deleteByUserId(authUser.getId());
        
        // Generate reset token (valid for 1 hour)
        String resetToken = UUID.randomUUID().toString();
        String tokenHash = jwtService.hashToken(resetToken);
        
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(authUser);
        passwordResetToken.setTokenHash(tokenHash);
        passwordResetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        
        passwordResetTokenRepository.save(passwordResetToken);
        
        System.out.println("Password reset token for " + request.getEmail() + ": " + resetToken);
    }
    
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = jwtService.hashToken(request.getToken());
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Reset token expired");
        }
        
        AuthUser authUser = resetToken.getUser();
        authUser.setPasswordHash(passwordService.hashPassword(request.getNewPassword()));
        authUserRepository.save(authUser);
        
        passwordResetTokenRepository.delete(resetToken);
        
        refreshTokenRepository.deleteByUserId(authUser.getId());
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
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