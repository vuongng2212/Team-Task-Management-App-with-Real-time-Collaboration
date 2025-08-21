# TaskFlow - Auth API

## Overview

The Auth API provides endpoints for user authentication, registration, and session management.

Base URL: `/api/auth`

## Authentication

Most endpoints in this service do not require authentication since they handle authentication itself. However, token refresh requires a valid refresh token.

## Endpoints

### User Registration
```
POST /api/auth/register
```

Registers a new user account.

**Request Body:**
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": "string",
      "name": "string",
      "email": "string",
      "createdAt": "timestamp"
    },
    "token": "jwt_access_token",
    "refreshToken": "jwt_refresh_token"
  }
}
```

**Error Responses:**
- 400: Validation error (missing fields, invalid email format, weak password)
- 409: User with this email already exists

### User Login
```
POST /api/auth/login
```

Authenticates a user and returns access tokens.

**Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "string",
      "name": "string",
      "email": "string"
    },
    "token": "jwt_access_token",
    "refreshToken": "jwt_refresh_token"
  }
}
```

**Error Responses:**
- 400: Validation error (missing fields)
- 401: Invalid credentials
- 429: Too many failed login attempts

### Token Refresh
```
POST /api/auth/refresh
```

Refreshes an expired access token using a refresh token.

**Request Headers:**
```
Authorization: Bearer refresh_token
```

**Response:**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "token": "new_jwt_access_token",
    "refreshToken": "new_jwt_refresh_token"
  }
}
```

**Error Responses:**
- 400: Missing or invalid refresh token
- 401: Expired or revoked refresh token

### Password Reset Request
```
POST /api/auth/reset-password/request
```

Sends a password reset email to the user.

**Request Body:**
```json
{
  "email": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset email sent"
}
```

**Error Responses:**
- 400: Validation error (missing email)
- 404: User with this email not found

### Password Reset
```
POST /api/auth/reset-password
```

Resets the user's password using a reset token.

**Request Body:**
```json
{
  "token": "string",
  "newPassword": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset successfully"
}
```

**Error Responses:**
- 400: Validation error (missing fields, weak password)
- 401: Invalid or expired reset token

### Logout
```
POST /api/auth/logout
```

Invalidates the user's refresh token.

**Request Headers:**
```
Authorization: Bearer refresh_token
```

**Response:**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

## Request Validation

### Registration Validation
- `name`: Required, 1-255 characters
- `email`: Required, valid email format, unique
- `password`: Required, minimum 8 characters with:
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one number
  - At least one special character

### Login Validation
- `email`: Required, valid email format
- `password`: Required

### Password Reset Request Validation
- `email`: Required, valid email format

### Password Reset Validation
- `token`: Required, valid JWT token
- `newPassword`: Required, minimum 8 characters with:
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one number
  - At least one special character

## Rate Limiting

- Registration: 5 requests per hour per IP
- Login: 10 failed attempts per hour per IP
- Password reset request: 3 requests per hour per email
- Password reset: 5 requests per hour per IP
- Token refresh: 100 requests per hour per token

## Security Considerations

1. All passwords must be hashed using bcrypt (cost factor ≥ 12)
2. JWT tokens must be properly signed and validated
3. Refresh tokens must be stored securely and invalidated on logout
4. Password reset tokens must expire within 1 hour
5. All communication must be over HTTPS
6. Implement proper CORS policies
7. Sanitize all input to prevent injection attacks

## Error Codes

| Code | Description |
|------|-------------|
| INVALID_CREDENTIALS | Incorrect email or password |
| USER_EXISTS | Email already registered |
| TOKEN_EXPIRED | JWT token has expired |
| TOKEN_INVALID | JWT token is invalid |
| TOKEN_REVOKED | JWT token has been revoked |
| RATE_LIMITED | Too many requests from this IP |
| VALIDATION_ERROR | Input data failed validation |
| USER_NOT_FOUND | User with specified email not found |
| RESET_TOKEN_EXPIRED | Password reset token has expired |
| RESET_TOKEN_INVALID | Password reset token is invalid |

This documentation provides detailed information about the Auth API endpoints. For implementation details, please refer to the Auth Service documentation.