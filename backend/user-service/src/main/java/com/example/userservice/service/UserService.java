package com.example.userservice.service;

import java.util.List;
import java.util.UUID;

import com.example.userservice.dto.request.CreateUserRequest;
import com.example.userservice.dto.request.UpdateUserRequest;
import com.example.userservice.dto.response.UserListResponse;
import com.example.userservice.dto.response.UserResponse;

public interface UserService {
    // API: Core endpoints from user-api.md
    UserResponse getCurrentUser(String authHeader);
    UserResponse getUserById(UUID id);
    UserResponse updateUser(UUID id, UpdateUserRequest request);
    UserListResponse searchUsers(String keyword, int page, int size);

    // INTERNAL: Auth service integration
    UserResponse createUserFromAuth(UUID userId, String email, String fullName);

    //INTERNAL: Other services integration
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserByEmail(String email);
    UserResponse updateAvatar(UUID id, String avatarUrl);
    void deleteUser(UUID id);
    List<UserResponse> getUsersByIds(List<UUID> ids);
    UserListResponse getAllUsers(int page, int size, String sortBy, String sortDirection);
    List<UserResponse> getTeamMembers();
    List<UserResponse> getAllUsers();
}
