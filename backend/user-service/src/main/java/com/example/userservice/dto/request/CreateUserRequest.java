package com.example.userservice.dto.request;

import com.example.userservice.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    private String fullName;
    private String email;
    private String avatarUrl;
    private UserRole role = UserRole.USER;
}
