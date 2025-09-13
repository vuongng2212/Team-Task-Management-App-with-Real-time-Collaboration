package com.example.userservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.dto.common.ApiResponse;
import com.example.userservice.dto.request.AddTeamMemberRequest;
import com.example.userservice.dto.request.CreateTeamRequest;
import com.example.userservice.dto.request.UpdateTeamMemberRoleRequest;
import com.example.userservice.dto.request.UpdateTeamRequest;
import com.example.userservice.dto.response.TeamMemberResponse;
import com.example.userservice.dto.response.TeamResponse;
import com.example.userservice.dto.response.UserTeamsResponse;
import com.example.userservice.exception.UserException.ForbiddenException;
import com.example.userservice.exception.UserException.UserNotFoundException;
import com.example.userservice.exception.UserException.ValidationException;
import com.example.userservice.service.TeamService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class TeamController {
    
    @Autowired
    private TeamService teamService;
    // API: POST /api/teams - theo user-api.md
    @PostMapping("/teams")
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @RequestBody CreateTeamRequest request,
            @RequestHeader("X-User-Id") UUID creatorId) { // TODO: Extract from JWT
        try {
            TeamResponse team = teamService.createTeam(request, creatorId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Team created successfully", team));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating team: {}", request.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create team"));
        }
    }
    
    // API: GET /api/teams/{id} - theo user-api.md
    @GetMapping("/teams/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID requestingUserId) { // TODO: Extract from JWT
        try {
            TeamResponse team = teamService.getTeamById(id, requestingUserId);
            return ResponseEntity.ok(ApiResponse.success(team));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting team: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve team"));
        }
    }
    
    // API: PUT /api/teams/{id} - theo user-api.md
    @PutMapping("/teams/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable UUID id,
            @RequestBody UpdateTeamRequest request,
            @RequestHeader("X-User-Id") UUID requestingUserId) { // TODO: Extract from JWT
        try {
            TeamResponse team = teamService.updateTeam(id, request, requestingUserId);
            return ResponseEntity.ok(ApiResponse.success("Team updated successfully", team));
        } catch (ValidationException e) {
            if (e.getMessage().contains("NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating team: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update team"));
        }
    }
    
    // API: DELETE /api/teams/{id} - theo user-api.md
    @DeleteMapping("/teams/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID requestingUserId) { // TODO: Extract from JWT
        try {
            teamService.deleteTeam(id, requestingUserId);
            return ResponseEntity.ok(ApiResponse.success("Team deleted successfully", null));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting team: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete team"));
        }
    }
    
    // API: GET /api/teams/{id}/members - theo user-api.md
    @GetMapping("/teams/{id}/members")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getTeamMembers(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID requestingUserId) { // TODO: Extract from JWT
        try {
            List<TeamMemberResponse> members = teamService.getTeamMembers(id, requestingUserId);
            return ResponseEntity.ok(ApiResponse.success(members));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting team members: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve team members"));
        }
    }
    
    // API: POST /api/teams/{id}/members - theo user-api.md
    @PostMapping("/teams/{id}/members")
    public ResponseEntity<ApiResponse<Void>> addTeamMember(
            @PathVariable UUID id,
            @RequestBody AddTeamMemberRequest request,
            @RequestHeader("X-User-Id") UUID requestingUserId) { // TODO: Extract from JWT
        try {
            teamService.addTeamMember(id, request, requestingUserId);
            return ResponseEntity.ok(ApiResponse.success("Member added to team successfully", null));
        } catch (ValidationException e) {
            if (e.getMessage().contains("NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding team member to team: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to add member"));
        }
    }
    
    // API: PUT /api/teams/{id}/members/{userId} - theo user-api.md
    @PutMapping("/teams/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> updateTeamMemberRole(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @RequestBody UpdateTeamMemberRoleRequest request,
            @RequestHeader("X-User-Id") UUID requestingUserId) { // TODO: Extract from JWT
        try {
            teamService.updateTeamMemberRole(id, userId, request, requestingUserId);
            return ResponseEntity.ok(ApiResponse.success("Member role updated successfully", null));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating member role in team: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update member role"));
        }
    }
    
    // API: DELETE /api/teams/{id}/members/{userId} - theo user-api.md
    @DeleteMapping("/teams/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeTeamMember(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @RequestHeader("X-User-Id") UUID requestingUserId) { // TODO: Extract from JWT
        try {
            teamService.removeTeamMember(id, userId, requestingUserId);
            return ResponseEntity.ok(ApiResponse.success("Member removed from team successfully", null));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error removing member from team: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to remove member"));
        }
    }
    
    // API: GET /api/users/{id}/teams - theo user-api.md
    @GetMapping("/users/{id}/teams")
    public ResponseEntity<ApiResponse<UserTeamsResponse>> getUserTeams(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID requestingUserId) { // TODO: Extract from JWT
        try {
            // SECURITY: Users can only view their own teams (theo user-api.md)
            if (!id.equals(requestingUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("FORBIDDEN: Users can only view their own teams"));
            }
            
            UserTeamsResponse teams = teamService.getUserTeams(id);
            return ResponseEntity.ok(ApiResponse.success(teams));
        } catch (Exception e) {
            log.error("Error getting user teams: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve user teams"));
        }
    }
}