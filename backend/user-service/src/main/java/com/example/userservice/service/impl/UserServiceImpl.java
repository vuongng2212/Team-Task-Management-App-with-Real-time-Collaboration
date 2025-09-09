package com.example.userservice.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
import com.example.userservice.exception.UserException.UserNotFoundException;
import com.example.userservice.exception.UserException.ValidationException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserConverter userConverter;

    private final ConcurrentHashMap<String, UserResponse> userCache = new ConcurrentHashMap<>();

    @Override
    public UserResponse getCurrentUser(String authHeader){
        throw new RuntimeException("JWT integration pending - use getUserById for now");
    }

    @Override
    public UserResponse getUserById(UUID id){
        log.debug("Getting user by id: {}", id);

        UserResponse cached = userCache.get(id.toString());
        if(cached != null){
            log.debug("Cache hit for user: {}", id);
            return cached;
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));

    UserResponse response = userConverter.toResponse(user);

    userCache.put(id.toString(), response);
    return response;
}

@Override
public UserResponse updateUser(UUID id, UpdateUserRequest request) {
    log.info("Updating user profile: {}", id);
    
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));
    
    validateUpdateRequest(request);
    
    userConverter.updateEntity(existingUser, request);
    User updatedUser = userRepository.save(existingUser);
    
    UserResponse response = userConverter.toResponse(updatedUser);
    
    userCache.put(id.toString(), response);
    
    log.info("User profile updated successfully: {}", id);
    return response;
}

@Override
public UserListResponse searchUsers(String keyword, int page, int size){
    log.debug("Searching users with keyword: {}, page: {}, size: {}", keyword, page, size);

    size = Math.min(size, 100);
    
    Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
    Page<User> userPage;

    if(keyword == null || keyword.trim().isEmpty()){
        userPage = userRepository.findAll(pageable);
    } else {
        userPage = userRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    List<UserResponse> userResponses = userConverter.toResponseList(userPage.getContent());

    return new UserListResponse(
        userResponses,
        (int) userPage.getTotalElements(),
        userPage.getNumber(),
        userPage.getSize()
    );
}

@Override
public UserResponse createUserFromAuth(UUID userId, String email, String fullName){
    log.info("Creating user from auth service: {} - {}", userId, email);
        

    if (userRepository.existsByEmail(email)){
        log.warn("User with email {} already exists", email);
        return getUserByEmail(email);
    }

    User user = new User();
    user.setId(userId);
    user.setEmail(email);
    user.setName(fullName);
    
    User savedUser = userRepository.save(user);
    UserResponse response = userConverter.toResponse(savedUser);
    userCache.put(userId.toString(), response);
    return response;
}

@Override
public UserResponse createUser(CreateUserRequest request){
    log.info("Creating user: {}", request.getEmail());

    if (userRepository.existsByEmail(request.getEmail())){
        throw new ValidationException("Email already exists: " + request.getEmail());
    }

    validateCreateRequest(request);
    User user = userConverter.toEntity(request);
    User savedUser = userRepository.save(user);

    UserResponse response = userConverter.toResponse(savedUser);
    userCache.put(savedUser.getId().toString(), response);
    return response;
}

@Override
public UserResponse getUserByEmail(String email){
    User user = userRepository.findByEmail(email)
    .orElseThrow(() -> new UserNotFoundException("email: " + email));
return userConverter.toResponse(user);
}

@Override
public UserResponse updateAvatar(UUID id, String avatarUrl) {
    log.info("Updating avatar for user: {}", id);
    
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));
    
    // VALIDATION: Avatar URL validation
    if (avatarUrl != null && !isValidUrl(avatarUrl)) {
        throw new ValidationException("Invalid avatar URL format");
    }
    
    user.setAvatar(avatarUrl);
    User updatedUser = userRepository.save(user);
    
    UserResponse response = userConverter.toResponse(updatedUser);
    userCache.put(id.toString(), response);
    
    return response;
}

@Override
public void deleteUser(UUID id) {
    log.info("Deleting user: {}", id);
    
    if (!userRepository.existsById(id)) {
        throw new UserNotFoundException(id.toString());
    }
    
    userRepository.deleteById(id);
    userCache.remove(id.toString());
    
    log.info("User deleted successfully: {}", id);
}

@Override
    public List<UserResponse> getUsersByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        
        // PERFORMANCE: Batch query
        List<User> users = userRepository.findAllByIdIn(ids);
        return userConverter.toResponseList(users);
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
    public List<UserResponse> getTeamMembers() {
        // BUSINESS: Get all users for team member selection
        List<User> users = userRepository.findAllByOrderByNameAsc();
        return userConverter.toResponseList(users);
    }
    
    @Override
    public List<UserResponse> getAllUsers() {
        return getTeamMembers();
    }

    private void validateCreateRequest(CreateUserRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        
        if (request.getName().length() > 255) {
            throw new ValidationException("Name too long (max 255 characters)");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (!isValidEmail(request.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        
        if (request.getBio() != null && request.getBio().length() > 1000) {
            throw new ValidationException("Bio too long (max 1000 characters)");
        }
        
        if (request.getAvatar() != null && !isValidUrl(request.getAvatar())) {
            throw new ValidationException("Invalid avatar URL format");
        }
    }

    private void validateUpdateRequest(UpdateUserRequest request) {
        // API: Validation theo user-api.md
        if (request.getName() != null) {
            if (request.getName().trim().isEmpty()) {
                throw new ValidationException("Name cannot be empty");
            }
            if (request.getName().length() > 255) {
                throw new ValidationException("Name too long (max 255 characters)");
            }
        }
        
        if (request.getBio() != null && request.getBio().length() > 1000) {
            throw new ValidationException("Bio too long (max 1000 characters)");
        }
        
        if (request.getAvatar() != null && !isValidUrl(request.getAvatar())) {
            throw new ValidationException("Invalid avatar URL format");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
    
    // PERFORMANCE: Clear cache method for maintenance
    public void clearUserCache(UUID userId) {
        userCache.remove(userId.toString());
        log.debug("Cleared cache for user: {}", userId);
    }
    
}