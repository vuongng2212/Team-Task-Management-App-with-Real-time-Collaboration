package com.example.userservice.service;

import java.util.List;
import java.util.UUID;

import com.example.userservice.dto.request.AddTeamMemberRequest;
import com.example.userservice.dto.request.CreateTeamRequest;
import com.example.userservice.dto.request.UpdateTeamMemberRoleRequest;
import com.example.userservice.dto.request.UpdateTeamRequest;
import com.example.userservice.dto.response.TeamMemberResponse;
import com.example.userservice.dto.response.TeamResponse;
import com.example.userservice.dto.response.UserTeamsResponse;

public interface TeamService {
    // API: Core team endpoint
    TeamResponse createTeam(CreateTeamRequest request, UUID creatorId);         // POST /api/teams
    TeamResponse getTeamById(UUID teamId, UUID requestingUserId);             // GET /api/teams/{id}
    TeamResponse updateTeam(UUID teamId, UpdateTeamRequest request, UUID requestingUserId); // PUT /api/teams/{id}
    void deleteTeam(UUID teamId, UUID requestingUserId);

    // API: Team membership endpoints
    UserTeamsResponse getUserTeams(UUID userId);                              // GET /api/users/{id}/teams
    List<TeamMemberResponse> getTeamMembers(UUID teamId, UUID requestingUserId); // GET /api/teams/{id}/members
    void addTeamMember(UUID teamId, AddTeamMemberRequest request, UUID requestingUserId); // POST /api/teams/{id}/members
    void updateTeamMemberRole(UUID teamId, UUID userId, UpdateTeamMemberRoleRequest request, UUID requestingUserId); // PUT /api/teams/{id}/members/{userId}
    void removeTeamMember(UUID teamId, UUID userId, UUID requestingUserId);   // DELETE /api/teams/{id}/members/{userId}

    // BUSINESS: Helper methods
    boolean isTeamMember(UUID teamId, UUID userId);
    boolean isTeamAdmin(UUID teamId, UUID userId);
}
