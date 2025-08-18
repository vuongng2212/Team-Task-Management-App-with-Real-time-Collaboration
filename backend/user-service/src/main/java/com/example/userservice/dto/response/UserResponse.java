package com.example.userservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.userservice.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
