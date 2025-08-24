package com.example.userservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private String name; // Đổi từ fullName thành name để phù hợp API
    private String email;
    private String avatar; // Đổi từ avatarUrl thành avatar để phù hợp API
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
