package com.example.authservice.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authservice.repository.PasswordResetTokenRepository;
import com.example.authservice.repository.RefreshTokenRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenCleanupService {
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            refreshTokenRepository.deleteExpiredTokens(now);
            log.info("Cleaned up expired refresh tokens at {}", now);
            
            passwordResetTokenRepository.deleteExpiredTokens(now);
            log.info("Cleaned up expired password reset tokens at {}", now);
            
        } catch (Exception e) {
            log.error("Error during token cleanup: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void dailyTokenCleanup() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            refreshTokenRepository.deleteExpiredTokens(now);
            passwordResetTokenRepository.deleteExpiredTokens(now);
            
            log.info("Daily token cleanup completed at {}", now);
            
        } catch (Exception e) {
            log.error("Error during daily token cleanup: {}", e.getMessage(), e);
        }
    }
}