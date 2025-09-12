package com.example.userservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.userservice.entity.Team;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    // BUSINESS: Team name uniqueness theo constraint
    boolean existsByName(String name);
    
    Optional<Team> findByName(String name);
    
    // BUSINESS: Find teams by creator/admin
    @Query("SELECT DISTINCT t FROM Team t " +
        "JOIN TeamMember tm ON t.id = tm.teamId " +
        "WHERE tm.userId = :userId AND tm.role = 'ADMIN'")
    List<Team> findTeamsByAdmin(@Param("userId") UUID userId);
    
    // BUSINESS: Find teams where user is member
    @Query("SELECT DISTINCT t FROM Team t " +
        "JOIN TeamMember tm ON t.id = tm.teamId " +
        "WHERE tm.userId = :userId")
    List<Team> findTeamsByUser(@Param("userId") UUID userId);

}
