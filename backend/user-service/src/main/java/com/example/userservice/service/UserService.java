package com.example.userservice.service;

import java.util.List;
import java.util.UUID;

import com.example.userservice.dto.request.CreateUserRequest;
import com.example.userservice.dto.request.UpdateUserRequest;
import com.example.userservice.dto.response.UserListResponse;
import com.example.userservice.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(UUID id);
    UserResponse getUserByEmail(String email);
    UserResponse getCurrentUser(String authHeader); // Get current user from JWT
    UserResponse updateUser(UUID id, UpdateUserRequest request); 
    UserResponse updateAvatar(UUID id, String avatarUrl);
    void deleteUser(UUID id); 

    UserListResponse getAllUsers(int page, int size, String sortBy, String sortDirection); 
    
    UserResponse createUserFromAuth(UUID userId, String email, String fullName);

    List<UserResponse> getUsersByIds(List<UUID> ids);

    UserListResponse searchUsers(String keyword, int page, int size);

    List<UserResponse> getTeamMembers();

    List<UserResponse> getAllUsers(); 
}
