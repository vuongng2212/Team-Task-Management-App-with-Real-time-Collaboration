package com.example.userservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_members")
@Data
@NoArgsConstructor
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "team_id", nullable = false)
    private UUID teamId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'MEMBER'")
    private TeamRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // SCHEMA: Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) {
            role = TeamRole.MEMBER;
        }
    }
    
    // BUSINESS: Helper methods
    public boolean isAdmin() {
        return role == TeamRole.ADMIN;
    }
    
    public boolean isMember() {
        return role == TeamRole.MEMBER;
    }
}
