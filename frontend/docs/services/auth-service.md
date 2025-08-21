# TaskFlow - Auth Service

## Overview

The Auth Service is responsible for user authentication, registration, and JWT token management. It handles all aspects of user identity verification and session management.

## Key Responsibilities

1. User registration and account creation
2. User login and authentication
3. JWT token generation and validation
4. Password reset functionality
5. Session management
6. Two-factor authentication (future enhancement)

## API Endpoints

### User Registration
```
POST /api/auth/register
```

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
    "token": "jwt_token"
  }
}
```

### User Login
```
POST /api/auth/login
```

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
    "token": "jwt_token"
  }
}
```

### Token Refresh
```
POST /api/auth/refresh
```

**Request Headers:**
```
Authorization: Bearer <refresh_token>
```

**Response:**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "token": "new_jwt_token"
  }
}
```

### Password Reset Request
```
POST /api/auth/reset-password/request
```

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

### Password Reset
```
POST /api/auth/reset-password
```

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

## Data Models

### User
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### Refresh Token
```sql
CREATE TABLE refresh_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  token_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### Password Reset Token
```sql
CREATE TABLE password_reset_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  token_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

## Business Logic

### Password Security
1. Passwords must be hashed using bcrypt with a cost factor of at least 12
2. Passwords must meet minimum security requirements:
   - At least 8 characters
   - Contains at least one uppercase letter
   - Contains at least one lowercase letter
   - Contains at least one number
   - Contains at least one special character

### JWT Token Management
1. Access tokens should have a short expiration time (e.g., 15 minutes)
2. Refresh tokens should have a longer expiration time (e.g., 7 days)
3. Tokens should be properly validated before granting access
4. Revoked tokens should be blacklisted to prevent reuse

### Rate Limiting
1. Implement rate limiting for authentication endpoints to prevent brute force attacks
2. Limit to 5 failed attempts per IP per minute
3. Temporary IP ban after 10 failed attempts in 10 minutes

## Security Considerations

1. All communication must be over HTTPS
2. Passwords must never be logged or stored in plain text
3. JWT tokens must be properly signed and validated
4. Implement proper CORS policies
5. Sanitize all input to prevent injection attacks
6. Implement proper error handling to avoid information leakage

## Error Handling

Common error responses:

```json
{
  "success": false,
  "message": "Error description",
  "error": {
    "code": "ERROR_CODE",
    "details": "Additional error details"
  }
}
```

Error codes:
- `INVALID_CREDENTIALS`: Incorrect email or password
- `USER_EXISTS`: Email already registered
- `TOKEN_EXPIRED`: JWT token has expired
- `TOKEN_INVALID`: JWT token is invalid
- `RATE_LIMITED`: Too many requests from this IP

## Deployment

### Environment Variables
```
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRES_IN=900  # 15 minutes
REFRESH_TOKEN_SECRET=your_refresh_token_secret
REFRESH_TOKEN_EXPIRES_IN=604800  # 7 days
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
```

### Health Check
```
GET /api/auth/health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "ISO_timestamp",
  "service": "auth-service"
}
```

## Testing

### Unit Tests
1. Password hashing and validation
2. JWT token generation and validation
3. User registration and login flows
4. Password reset functionality

### Integration Tests
1. End-to-end authentication flow
2. Token refresh mechanism
3. Database interactions
4. Rate limiting enforcement

This documentation provides a comprehensive guide for implementing the Auth Service. For any questions or clarifications, please consult with the project team.