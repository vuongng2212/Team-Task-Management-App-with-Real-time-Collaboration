package com.example.userservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.userservice.entity.TeamMember;
import com.example.userservice.entity.TeamRole;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
        // BUSINESS: Core team membership queries
        List<TeamMember> findByTeamId(UUID teamId);
    
        List<TeamMember> findByUserId(UUID userId);
        
        Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId);
        
        boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);
        
        // BUSINESS: Role-based queries
        List<TeamMember> findByTeamIdAndRole(UUID teamId, TeamRole role);
        
        @Query("SELECT COUNT(tm) FROM TeamMember tm WHERE tm.teamId = :teamId AND tm.role = 'ADMIN'")
        long countAdminsByTeamId(@Param("teamId") UUID teamId);
        
        @Query("SELECT COUNT(tm) FROM TeamMember tm WHERE tm.teamId = :teamId")
        long countMembersByTeamId(@Param("teamId") UUID teamId);
        
        // BUSINESS: Delete operations
        void deleteByTeamIdAndUserId(UUID teamId, UUID userId);
        
        void deleteByTeamId(UUID teamId);

}
