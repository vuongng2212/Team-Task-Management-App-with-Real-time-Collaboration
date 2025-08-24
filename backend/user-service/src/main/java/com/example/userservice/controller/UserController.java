package com.example.userservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.userservice.dto.common.ApiResponse;
import com.example.userservice.dto.request.CreateUserFromAuthRequest;
import com.example.userservice.dto.request.CreateUserRequest;
import com.example.userservice.dto.request.UpdateAvatarRequest;
import com.example.userservice.dto.request.UpdateUserRequest;
import com.example.userservice.dto.response.UserListResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    // GET /api/users/me - Get Current User Profile (from JWT token)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UserResponse user = userService.getCurrentUser(authHeader);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Unauthorized"));
        }
    }

    // GET /api/users/{id} - Get User Profile
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found"));
        }
    }
    
    // PUT /api/users/{id} - Update User Profile
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id, 
            @RequestBody UpdateUserRequest request) {
        try {
            UserResponse user = userService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // GET /api/users - Search Users with pagination
    @GetMapping
    public ResponseEntity<ApiResponse<UserListResponse>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            // Convert to 0-based page for internal use
            int zeroBased = Math.max(0, page - 1);
            UserListResponse response;
            
            if (query != null && !query.trim().isEmpty()) {
                response = userService.searchUsers(query.trim(), zeroBased, limit);
            } else {
                response = userService.getAllUsers(zeroBased, limit, "name", "ASC");
            }
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // ========== EXISTING ENDPOINTS FOR INTERNAL USE ==========
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        try {
            UserResponse user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        try {
            UserResponse user = userService.getUserByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(
            @PathVariable UUID id, 
            @RequestBody UpdateAvatarRequest request) {
        try {
            UserResponse user = userService.updateAvatar(id, request.getAvatarUrl());
            return ResponseEntity.ok(ApiResponse.success("Avatar updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // TODO: Remove role endpoint - roles are now managed via team_members table
    // @GetMapping("/role/{role}") - deprecated
    
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
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/batch")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByIds(@RequestParam List<UUID> ids) {
        try {
            List<UserResponse> users = userService.getUsersByIds(ids);
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    

    @GetMapping("/team-members")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getTeamMembers() {
        try {
            List<UserResponse> teamMembers = userService.getTeamMembers();
            return ResponseEntity.ok(ApiResponse.success(teamMembers));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}