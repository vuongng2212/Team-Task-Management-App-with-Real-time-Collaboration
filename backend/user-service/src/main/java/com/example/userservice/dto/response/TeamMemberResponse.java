package com.example.userservice.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamMemberResponse {
    private UUID id;
    private String name;
    private String email;
    private String avatar;
    private String role;  
}