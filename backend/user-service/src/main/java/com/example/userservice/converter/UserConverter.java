package com.example.userservice.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.userservice.dto.request.CreateUserRequest;
import com.example.userservice.dto.request.UpdateUserRequest;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.entity.User;

@Component
public class UserConverter {
    
    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setBio(request.getBio());
        user.setAvatar(request.getAvatar());
        return user;
    }
    
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
    
    public void updateEntity(User existingUser, UpdateUserRequest request) {
        // API: Only allow updating specific fields per user-api.md
        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }
        if (request.getBio() != null) {
            existingUser.setBio(request.getBio());
        }
        if (request.getAvatar() != null) {
            existingUser.setAvatar(request.getAvatar());
        }
    }
    
    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
