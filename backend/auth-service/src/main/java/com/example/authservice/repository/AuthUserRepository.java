package com.example.authservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.authservice.entity.AuthUser;

/**
 * Auth user data access
 * 
 * PERFORMANCE: Indexed queries for common operations
 * BUSINESS: Active user filtering in most queries
 * PATTERN: Similar to UserRepository with practical queries
 */
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    
    // BUSINESS: Core authentication queries
    Optional<AuthUser> findByEmailAndIsActiveTrue(String email);
    
    Optional<AuthUser> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    // ADMIN: User management queries
    @Query("SELECT u FROM AuthUser u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    List<AuthUser> findAllActiveUsers();
    
    @Query("SELECT u FROM AuthUser u WHERE u.failedLoginAttempts >= :attempts")
    List<AuthUser> findUsersWithFailedAttempts(@Param("attempts") Integer attempts);
    
    @Query("SELECT u FROM AuthUser u WHERE u.isActive = true AND u.lastSuccessfulLogin > :since")
    List<AuthUser> findActiveUsersSince(@Param("since") LocalDateTime since);
    
    // CLEANUP: Maintenance queries
    @Query("SELECT u FROM AuthUser u WHERE u.isActive = false AND u.updatedAt < :cutoffDate")
    List<AuthUser> findInactiveUsersBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // SECURITY: Reset failed attempts for unlocked accounts
    @Modifying
    @Query("UPDATE AuthUser u SET u.failedLoginAttempts = 0, u.lastLoginAttempt = null WHERE u.lastLoginAttempt < :cutoffTime")
    int resetExpiredLockouts(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // METRICS: Count queries
    @Query("SELECT COUNT(u) FROM AuthUser u WHERE u.isActive = true")
    long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM AuthUser u WHERE u.failedLoginAttempts >= 5")
    long countLockedUsers();
    
    // ADMIN: Find users by various criteria
    @Query("SELECT u FROM AuthUser u WHERE u.email LIKE %:emailPattern% AND u.isActive = true")
    List<AuthUser> findActiveUsersByEmailPattern(@Param("emailPattern") String emailPattern);
    
    @Query("SELECT u FROM AuthUser u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<AuthUser> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 