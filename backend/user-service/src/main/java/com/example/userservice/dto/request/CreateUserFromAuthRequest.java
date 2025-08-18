package com.example.userservice.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserFromAuthRequest {
    private UUID userId;
    private String fullName;
    private String email;
}
