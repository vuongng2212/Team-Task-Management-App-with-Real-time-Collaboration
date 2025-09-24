package com.example.authservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Refresh token storage for session management
 * 
 * SECURITY: Tokens are hashed, not stored in plain text
 * CLEANUP: Expired tokens should be periodically removed
 * PATTERN: Similar to User entity with business methods
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUser user;
    
    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // BUSINESS: Check if token is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    // BUSINESS: Get remaining validity time in minutes
    public long getRemainingMinutes() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }
    
    // BUSINESS: Check if token expires soon (within 1 hour)
    public boolean expiresSoon() {
        return getRemainingMinutes() <= 60;
    }
    
    // BUSINESS: Get token status for logging
    public String getTokenStatus() {
        if (isExpired()) {
            return "EXPIRED";
        }
        if (expiresSoon()) {
            return "EXPIRES_SOON";
        }
        return "VALID";
    }
} 