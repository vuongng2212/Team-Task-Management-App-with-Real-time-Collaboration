package com.example.userservice.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.dto.request.AddTeamMemberRequest;
import com.example.userservice.dto.request.CreateTeamRequest;
import com.example.userservice.dto.request.UpdateTeamMemberRoleRequest;
import com.example.userservice.dto.request.UpdateTeamRequest;
import com.example.userservice.dto.response.TeamMemberResponse;
import com.example.userservice.dto.response.TeamResponse;
import com.example.userservice.dto.response.UserTeamsResponse;
import com.example.userservice.entity.Team;
import com.example.userservice.entity.TeamMember;
import com.example.userservice.entity.TeamRole;
import com.example.userservice.entity.User;
import com.example.userservice.exception.UserException.ForbiddenException;
import com.example.userservice.exception.UserException.UserNotFoundException;
import com.example.userservice.exception.UserException.ValidationException;
import com.example.userservice.repository.TeamMemberRepository;
import com.example.userservice.repository.TeamRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.TeamService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TeamServiceImpl implements TeamService {
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public TeamResponse createTeam(CreateTeamRequest request, UUID creatorId) {
        log.info("Creating team '{}' by user: {}", request.getName(), creatorId);
        
        // VALIDATION: Input validation theo user-api.md
        validateCreateTeamRequest(request);
        
        // BUSINESS: Team name uniqueness
        if (teamRepository.existsByName(request.getName())) {
            throw new ValidationException("TEAM_ALREADY_EXISTS: Team with this name already exists");
        }
        
        // BUSINESS: Verify creator exists
        if (!userRepository.existsById(creatorId)) {
            throw new UserNotFoundException(creatorId.toString());
        }
        
        // CREATE: Team
        Team team = new Team();
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        Team savedTeam = teamRepository.save(team);
        
        // BUSINESS: Add creator as admin
        TeamMember adminMember = new TeamMember();
        adminMember.setTeamId(savedTeam.getId());
        adminMember.setUserId(creatorId);
        adminMember.setRole(TeamRole.ADMIN);
        teamMemberRepository.save(adminMember);
        
        log.info("Team created successfully: {} (ID: {})", savedTeam.getName(), savedTeam.getId());
        
        return toTeamResponse(savedTeam, null);
    }
    
    @Override
    public TeamResponse getTeamById(UUID teamId, UUID requestingUserId) {
        log.debug("Getting team details: {} for user: {}", teamId, requestingUserId);
        
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ValidationException("TEAM_NOT_FOUND: Team not found"));
        
        // SECURITY: Verify user is team member
        if (!isTeamMember(teamId, requestingUserId)) {
            throw new ForbiddenException("User not a member of this team");
        }
        
        // BUSINESS: Get team with members
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        List<TeamMemberResponse> memberResponses = members.stream()
            .map(this::toTeamMemberResponse)
            .collect(Collectors.toList());
        
        TeamResponse response = toTeamResponse(team, memberResponses);
        return response;
    }
    
    @Override
    public TeamResponse updateTeam(UUID teamId, UpdateTeamRequest request, UUID requestingUserId) {
        log.info("Updating team: {} by user: {}", teamId, requestingUserId);
        
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ValidationException("TEAM_NOT_FOUND: Team not found"));
        
        // SECURITY: Only team admins can update
        if (!isTeamAdmin(teamId, requestingUserId)) {
            throw new ForbiddenException("Only team admins can update team");
        }
        
        // VALIDATION: Input validation
        validateUpdateTeamRequest(request);
        
        // BUSINESS: Name uniqueness (excluding current team)
        if (request.getName() != null && !request.getName().equals(team.getName())) {
            if (teamRepository.existsByName(request.getName())) {
                throw new ValidationException("TEAM_ALREADY_EXISTS: Team with this name already exists");
            }
        }
        
        // UPDATE: Allowed fields
        if (request.getName() != null) {
            team.setName(request.getName());
        }
        if (request.getDescription() != null) {
            team.setDescription(request.getDescription());
        }
        
        Team updatedTeam = teamRepository.save(team);
        
        log.info("Team updated successfully: {}", teamId);
        return toTeamResponse(updatedTeam, null);
    }
    
    @Override
    public void deleteTeam(UUID teamId, UUID requestingUserId) {
        log.info("Deleting team: {} by user: {}", teamId, requestingUserId);
        
        if (!teamRepository.existsById(teamId)) {
            throw new ValidationException("TEAM_NOT_FOUND: Team not found");
        }
        
        // SECURITY: Only team admins can delete
        if (!isTeamAdmin(teamId, requestingUserId)) {
            throw new ForbiddenException("Only team admins can delete team");
        }
        
        // DELETE: Team and all memberships (CASCADE)
        teamMemberRepository.deleteByTeamId(teamId);
        teamRepository.deleteById(teamId);
        
        log.info("Team deleted successfully: {}", teamId);
    }
    
    @Override
    public UserTeamsResponse getUserTeams(UUID userId) {
        log.debug("Getting teams for user: {}", userId);
        
        // BUSINESS: Get teams where user is member
        List<Team> teams = teamRepository.findTeamsByUser(userId);
        
        List<TeamResponse> teamResponses = teams.stream()
            .map(team -> {
                // Get user's role in this team
                TeamMember membership = teamMemberRepository.findByTeamIdAndUserId(team.getId(), userId)
                    .orElse(null);
                
                // Get member count
                long memberCount = teamMemberRepository.countMembersByTeamId(team.getId());
                
                TeamResponse response = toTeamResponse(team, null);
                response.setRole(membership != null ? membership.getRole().name().toLowerCase() : null);
                response.setMembersCount((int) memberCount);
                
                return response;
            })
            .collect(Collectors.toList());
        
        return new UserTeamsResponse(teamResponses);
    }
    
    @Override
    public List<TeamMemberResponse> getTeamMembers(UUID teamId, UUID requestingUserId) {
        log.debug("Getting team members for team: {} by user: {}", teamId, requestingUserId);
        
        if (!teamRepository.existsById(teamId)) {
            throw new ValidationException("TEAM_NOT_FOUND: Team not found");
        }
        
        // SECURITY: Verify user is team member
        if (!isTeamMember(teamId, requestingUserId)) {
            throw new ForbiddenException("User not a member of this team");
        }
        
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        return members.stream()
            .map(this::toTeamMemberResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void addTeamMember(UUID teamId, AddTeamMemberRequest request, UUID requestingUserId) {
        log.info("Adding member {} to team: {} by user: {}", request.getUserId(), teamId, requestingUserId);
        
        if (!teamRepository.existsById(teamId)) {
            throw new ValidationException("TEAM_NOT_FOUND: Team not found");
        }
        
        // SECURITY: Only team admins can add members
        if (!isTeamAdmin(teamId, requestingUserId)) {
            throw new ForbiddenException("Only team admins can add members");
        }
        
        // VALIDATION: User exists
        if (!userRepository.existsById(request.getUserId())) {
            throw new UserNotFoundException(request.getUserId().toString());
        }
        
        // BUSINESS: Check if already member
        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, request.getUserId())) {
            throw new ValidationException("USER_ALREADY_MEMBER: User is already a member of this team");
        }
        
        // VALIDATION: Role validation
        TeamRole role;
        try {
            role = TeamRole.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Invalid role. Must be 'admin' or 'member'");
        }
        
        // CREATE: Team membership
        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(request.getUserId());
        member.setRole(role);
        teamMemberRepository.save(member);
        
        log.info("Member added successfully to team: {}", teamId);
    }
    
    @Override
    public void updateTeamMemberRole(UUID teamId, UUID userId, UpdateTeamMemberRoleRequest request, UUID requestingUserId) {
        log.info("Updating role for user {} in team: {} by user: {}", userId, teamId, requestingUserId);
        
        // SECURITY: Only team admins can update roles
        if (!isTeamAdmin(teamId, requestingUserId)) {
            throw new ForbiddenException("Only team admins can update member roles");
        }
        
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
            .orElseThrow(() -> new ValidationException("User not found in team"));
        
        // VALIDATION: Role validation
        TeamRole role;
        try {
            role = TeamRole.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Invalid role. Must be 'admin' or 'member'");
        }
        
        member.setRole(role);
        teamMemberRepository.save(member);
        
        log.info("Member role updated successfully in team: {}", teamId);
    }
    
    @Override
    public void removeTeamMember(UUID teamId, UUID userId, UUID requestingUserId) {
        log.info("Removing user {} from team: {} by user: {}", userId, teamId, requestingUserId);
        
        // SECURITY: Only team admins can remove members
        if (!isTeamAdmin(teamId, requestingUserId)) {
            throw new ForbiddenException("Only team admins can remove members");
        }
        
        // BUSINESS: Admins cannot remove themselves (must transfer ownership first)
        if (userId.equals(requestingUserId)) {
            throw new ForbiddenException("CANNOT_REMOVE_SELF: Admins cannot remove themselves from team");
        }
        
        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new ValidationException("User not found in team");
        }
        
        teamMemberRepository.deleteByTeamIdAndUserId(teamId, userId);
        
        log.info("Member removed successfully from team: {}", teamId);
    }
    
    @Override
    public boolean isTeamMember(UUID teamId, UUID userId) {
        return teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
    }
    
    @Override
    public boolean isTeamAdmin(UUID teamId, UUID userId) {
        return teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
            .map(member -> member.getRole() == TeamRole.ADMIN)
            .orElse(false);
    }
    
    private TeamResponse toTeamResponse(Team team, List<TeamMemberResponse> members) {
        TeamResponse response = new TeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setDescription(team.getDescription());
        response.setCreatedAt(team.getCreatedAt());
        response.setUpdatedAt(team.getUpdatedAt());
        response.setMembers(members);
        return response;
    }
    
    private TeamMemberResponse toTeamMemberResponse(TeamMember member) {
        // Get user details
        User user = userRepository.findById(member.getUserId()).orElse(null);
        
        TeamMemberResponse response = new TeamMemberResponse();
        response.setId(member.getUserId());
        response.setRole(member.getRole().name().toLowerCase());
        
        if (user != null) {
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setAvatar(user.getAvatar());
        }
        
        return response;
    }
    
    // VALIDATION: Methods
    private void validateCreateTeamRequest(CreateTeamRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Team name is required");
        }
        
        if (request.getName().length() > 255) {
            throw new ValidationException("Team name too long (max 255 characters)");
        }
        
        if (request.getDescription() != null && request.getDescription().length() > 1000) {
            throw new ValidationException("Team description too long (max 1000 characters)");
        }
    }
    
    private void validateUpdateTeamRequest(UpdateTeamRequest request) {
        if (request.getName() != null) {
            if (request.getName().trim().isEmpty()) {
                throw new ValidationException("Team name cannot be empty");
            }
            if (request.getName().length() > 255) {
                throw new ValidationException("Team name too long (max 255 characters)");
            }
        }
        
        if (request.getDescription() != null && request.getDescription().length() > 1000) {
            throw new ValidationException("Team description too long (max 1000 characters)");
        }
    }
}