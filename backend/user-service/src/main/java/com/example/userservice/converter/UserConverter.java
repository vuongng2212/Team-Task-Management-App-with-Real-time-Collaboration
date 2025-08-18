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
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setRole(request.getRole());
        return user;
    }

    public User toEntity(UpdateUserRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setRole(request.getRole());
        return user;
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRole(user.getRole());
        return response;
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public void updateEntity(User existingUser, UpdateUserRequest request) {
        existingUser.setFullName(request.getFullName());
        existingUser.setEmail(request.getEmail());
        existingUser.setAvatarUrl(request.getAvatarUrl());
        if (request.getRole() != null) {
            existingUser.setRole(request.getRole());
        }
    }
}
