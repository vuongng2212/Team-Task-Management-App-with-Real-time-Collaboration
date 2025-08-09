package com.example.userservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    boolean existsByEmail(String email);
    
    // Pagination support methods
    Page<User> findByRole(UserRole role, Pageable pageable);
    Page<User> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
}