package com.example.userservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.converter.UserConverter;
import com.example.userservice.dto.request.CreateUserRequest;
import com.example.userservice.dto.request.UpdateUserRequest;
import com.example.userservice.dto.response.UserListResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserConverter userConverter;
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = userConverter.toEntity(request);
        User savedUser = userRepository.save(user);
        return userConverter.toResponse(savedUser);
    }
    
    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return userConverter.toResponse(user);
    }
    
    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return userConverter.toResponse(user);
    }
    
    @Override
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        userConverter.updateEntity(existingUser, request);
        User updatedUser = userRepository.save(existingUser);
        return userConverter.toResponse(updatedUser);
    }
    
    @Override
    public UserResponse updateAvatar(UUID id, String avatarUrl) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setAvatarUrl(avatarUrl);
        User updatedUser = userRepository.save(user);
        return userConverter.toResponse(updatedUser);
    }
    
    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserListResponse getAllUsers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses = userConverter.toResponseList(userPage.getContent());
        
        return new UserListResponse(
            userResponses,
            (int) userPage.getTotalElements(),
            userPage.getNumber(),
            userPage.getSize()
        );
    }
    
    @Override
    public UserListResponse getUsersByRole(UserRole role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByRole(role, pageable);
        List<UserResponse> userResponses = userConverter.toResponseList(userPage.getContent());
        
        return new UserListResponse(
            userResponses,
            (int) userPage.getTotalElements(),
            userPage.getNumber(),
            userPage.getSize()
        );
    }
    
    @Override
    public boolean isAdmin(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return UserRole.ADMIN.equals(user.getRole());
    }
    
    @Override
    public boolean isUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return UserRole.USER.equals(user.getRole());
    }
    
    @Override
    public UserResponse createUserFromAuth(UUID userId, String email, String fullName) {
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(UserRole.USER);
        
        User savedUser = userRepository.save(user);
        return userConverter.toResponse(savedUser);
    }
    
    @Override
    public List<UserResponse> getUsersByIds(List<UUID> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userConverter.toResponseList(users);
    }
    
    @Override
    public UserListResponse searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByFullNameContainingIgnoreCase(keyword, pageable);
        List<UserResponse> userResponses = userConverter.toResponseList(userPage.getContent());
        
        return new UserListResponse(
            userResponses,
            (int) userPage.getTotalElements(),
            userPage.getNumber(),
            userPage.getSize()
        );
    }
    
    @Override
    public List<UserResponse> getTeamMembers() {
        List<User> teamMembers = userRepository.findByRole(UserRole.USER);
        return userConverter.toResponseList(teamMembers);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userConverter.toResponseList(users);
    }
    
    @Override
    public List<UserResponse> getUsersByRole(UserRole role) {
        List<User> users = userRepository.findByRole(role);
        return userConverter.toResponseList(users);
    }
} 