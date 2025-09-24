package com.example.authservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Core authentication user entity
 * 
 * DESIGN: Minimal auth data, profile data lives in user-service
 * SYNC: ID matches with user-service User entity
 * PATTERN: Similar to User entity with business methods
 */
@Entity
@Table(name = "auth_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    
    @Id
    private UUID id; // SYNC: Same ID as user in user-service
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "last_login_attempt")
    private LocalDateTime lastLoginAttempt;
    
    @Column(name = "last_successful_login")
    private LocalDateTime lastSuccessfulLogin;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (failedLoginAttempts == null) {
            failedLoginAttempts = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // BUSINESS: Check if account is locked due to failed attempts
    public boolean isLocked() {
        return failedLoginAttempts >= 5 && 
               lastLoginAttempt != null && 
               lastLoginAttempt.isAfter(LocalDateTime.now().minusMinutes(30));
    }
    
    // BUSINESS: Get remaining lockout minutes
    public int getRemainingLockoutMinutes() {
        if (!isLocked()) {
            return 0;
        }
        
        LocalDateTime lockoutEnd = lastLoginAttempt.plusMinutes(30);
        long minutesRemaining = java.time.Duration.between(LocalDateTime.now(), lockoutEnd).toMinutes();
        return Math.max(0, (int) minutesRemaining);
    }
    
    // BUSINESS: Reset failed login attempts
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lastLoginAttempt = null;
        this.lastSuccessfulLogin = LocalDateTime.now();
    }
    
    // BUSINESS: Record failed login attempt
    public void recordFailedAttempt() {
        this.failedLoginAttempts++;
        this.lastLoginAttempt = LocalDateTime.now();
    }
    
    // BUSINESS: Check if account needs attention
    public boolean needsPasswordChange() {
        // TODO: Implement password age policy
        return false;
    }
    
    // BUSINESS: Get account status for logging
    public String getAccountStatus() {
        if (!isActive) {
            return "INACTIVE";
        }
        if (isLocked()) {
            return "LOCKED";
        }
        return "ACTIVE";
    }
} 