package com.example.userservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.dto.common.ApiResponse;
import com.example.userservice.dto.request.CreateUserFromAuthRequest;
import com.example.userservice.dto.request.CreateUserRequest;
import com.example.userservice.dto.request.UpdateAvatarRequest;
import com.example.userservice.dto.request.UpdateUserRequest;
import com.example.userservice.dto.response.UserListResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.exception.UserException.UserNotFoundException;
import com.example.userservice.exception.UserException.ValidationException;
import com.example.userservice.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;

    // API: GET /api/users/me - theo user-api.md
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UserResponse user = userService.getCurrentUser(authHeader);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            log.warn("Failed to get current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("UNAUTHORIZED: Invalid or expired token"));
        }
    }

    // API: GET /api/users/{id} - theo user-api.md
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting user by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve user"));
        }
    }
    
    // API: PUT /api/users/{id} - theo user-api.md
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {
        try {
            UserResponse user = userService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update user"));
        }
    }
    
    // API: GET /api/users - theo user-api.md
    @GetMapping
    public ResponseEntity<ApiResponse<UserListResponse>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            // API: Validation theo user-api.md (max 100 per page)
            if (limit > 100) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("VALIDATION_ERROR: Limit cannot exceed 100"));
            }
            
            // Convert to 0-based page
            int zeroBased = Math.max(0, page - 1);
            
            UserListResponse response = userService.searchUsers(query, zeroBased, limit);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error searching users with query: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to search users"));
        }
    }

    // ========== INTERNAL ENDPOINTS (NOT IN PUBLIC API) ==========

    // API: POST /api/users - theo user-api.md
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        try {
            UserResponse user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", user));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating user: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create user"));
        }
    }

    // API: GET /api/users/email/{email} - theo user-api.md
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        try {
            UserResponse user = userService.getUserByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting user by email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve user"));
        }
    }

    // API: PATCH /api/users/{id}/avatar - theo user-api.md
    @PatchMapping("/{id}/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(
            @PathVariable UUID id,
            @RequestBody UpdateAvatarRequest request) {
        try {
            UserResponse user = userService.updateAvatar(id, request.getAvatarUrl());
            return ResponseEntity.ok(ApiResponse.success("Avatar updated successfully", user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating avatar for user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update avatar"));
        }
    }

    // API: DELETE /api/users/{id} - theo user-api.md
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete user"));
        }
    }

    // API: POST /api/users/from-auth - theo user-api.md
    @PostMapping("/from-auth")
    public ResponseEntity<ApiResponse<UserResponse>> createUserFromAuth(
            @RequestBody CreateUserFromAuthRequest request) {
        try {
            UserResponse user = userService.createUserFromAuth(
                request.getUserId(),
                request.getEmail(),
                request.getFullName()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created from auth successfully", user));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating user from auth: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create user"));
        }
    }

    // API: GET /api/users/batch - theo user-api.md
    @GetMapping("/batch")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByIds(@RequestParam List<UUID> ids) {
        try {
            if (ids.size() > 100) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("VALIDATION_ERROR: Too many IDs (max 100)"));
            }
            
            List<UserResponse> users = userService.getUsersByIds(ids);
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            log.error("Error getting users by IDs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve users"));
        }
    }

    // API: GET /api/users/team-members - theo user-api.md
    @GetMapping("/team-members")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getTeamMembers() {
        try {
            List<UserResponse> teamMembers = userService.getTeamMembers();
            return ResponseEntity.ok(ApiResponse.success(teamMembers));
        } catch (Exception e) {
            log.error("Error getting team members", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve team members"));
        }
    }
}