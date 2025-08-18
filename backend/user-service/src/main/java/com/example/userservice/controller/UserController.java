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
import com.example.userservice.entity.UserRole;
import com.example.userservice.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

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
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id, 
            @RequestBody UpdateUserRequest request) {
        try {
            UserResponse user = userService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
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
    
    @GetMapping
    public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        try {
            UserListResponse response = userService.getAllUsers(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<UserListResponse>> getUsersByRole(
            @PathVariable UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            UserListResponse response = userService.getUsersByRole(role, page, size);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
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
    
    @GetMapping("/{id}/role/admin")
    public ResponseEntity<ApiResponse<Boolean>> isAdmin(@PathVariable UUID id) {
        try {
            boolean isAdmin = userService.isAdmin(id);
            return ResponseEntity.ok(ApiResponse.success(isAdmin));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserListResponse>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            UserListResponse response = userService.searchUsers(keyword, page, size);
            return ResponseEntity.ok(ApiResponse.success(response));
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