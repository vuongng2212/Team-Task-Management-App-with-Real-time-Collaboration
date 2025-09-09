package com.example.userservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.userservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Search users by name or email
    @Query("SELECT u FROM User u WHERE"
    + "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
    + "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> findByNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

    
    @Query("SELECT u FROM User u WHERE u.id = :ids")
    List<User> findAllByIdIn(@Param("ids") List<UUID> ids);

    @Query("SELECT u FROM User u ORDER by u.name ASC")
    List<User> findAllByOrderByNameAsc();

}