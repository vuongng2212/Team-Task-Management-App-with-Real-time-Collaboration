package com.example.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String name; // 1-255 characters
    private String bio; // max 1000 characters 
    private String avatar; // URL for avatar
}
