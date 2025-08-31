package com.example.authservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authservice.entity.AuthUser;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    
    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<AuthUser> findByEmailAndIsActiveTrue(String email);
} 