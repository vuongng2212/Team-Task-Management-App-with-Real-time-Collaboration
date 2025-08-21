# TaskFlow - User Service

## Overview

The User Service manages user profiles, team information, and user-related data. It handles user details, team memberships, and profile updates.

## Key Responsibilities

1. User profile management
2. Team creation and management
3. Team membership handling
4. User search and filtering
5. Profile picture management
6. User preferences and settings

## API Endpoints

### Get User Profile
```
GET /api/users/:id
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "string",
    "name": "string",
    "email": "string",
    "avatar": "string",
    "bio": "string",
    "createdAt": "timestamp",
    "updatedAt": "timestamp"
  }
}
```

### Update User Profile
```
PUT /api/users/:id
```

**Request Body:**
```json
{
  "name": "string",
  "bio": "string",
  "avatar": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "user": {
      "id": "string",
      "name": "string",
      "email": "string",
      "avatar": "string",
      "bio": "string",
      "updatedAt": "timestamp"
    }
  }
}
```

### Get User Teams
```
GET /api/users/:id/teams
```

**Response:**
```json
{
  "success": true,
  "data": {
    "teams": [
      {
        "id": "string",
        "name": "string",
        "description": "string",
        "membersCount": "number",
        "role": "string"
      }
    ]
  }
}
```

### Search Users
```
GET /api/users?query=:searchTerm
```

**Response:**
```json
{
  "success": true,
  "data": {
    "users": [
      {
        "id": "string",
        "name": "string",
        "email": "string",
        "avatar": "string"
      }
    ]
  }
}
```

### Create Team
```
POST /api/teams
```

**Request Body:**
```json
{
  "name": "string",
  "description": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Team created successfully",
  "data": {
    "team": {
      "id": "string",
      "name": "string",
      "description": "string",
      "createdAt": "timestamp"
    }
  }
}
```

### Get Team Details
```
GET /api/teams/:id
```

**Response:**
```json
{
  "success": true,
  "data": {
    "team": {
      "id": "string",
      "name": "string",
      "description": "string",
      "createdAt": "timestamp",
      "members": [
        {
          "id": "string",
          "name": "string",
          "email": "string",
          "avatar": "string",
          "role": "string"
        }
      ]
    }
  }
}
```

### Add Team Member
```
POST /api/teams/:id/members
```

**Request Body:**
```json
{
  "userId": "string",
  "role": "string"  // admin, member
}
```

**Response:**
```json
{
  "success": true,
  "message": "Member added to team successfully"
}
```

### Remove Team Member
```
DELETE /api/teams/:id/members/:userId
```

**Response:**
```json
{
  "success": true,
  "message": "Member removed from team successfully"
}
```

## Data Models

### User
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  avatar VARCHAR(255),
  bio TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### Team
```sql
CREATE TABLE teams (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  description TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### Team Membership
```sql
CREATE TABLE team_members (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  team_id UUID REFERENCES teams(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(50) NOT NULL DEFAULT 'member',  -- admin, member
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(team_id, user_id)
);
```

## Business Logic

### User Profile Management
1. Users can only update their own profiles
2. Email addresses must be unique across the system
3. Avatar images should be properly validated and stored

### Team Management
1. Only team admins can add/remove members
2. Team names must be unique within the system
3. Users can be members of multiple teams
4. A team must have at least one admin

### Role-Based Access Control
1. Admins can manage team settings and members
2. Members can view team information and participate in projects
3. Ownership transfer must be handled when admins leave

## Security Considerations

1. All endpoints must be protected with JWT authentication
2. Users can only access their own profile information
3. Team membership must be verified for team-related operations
4. Input validation must be performed on all user-provided data
5. File uploads (avatars) must be properly sanitized and validated

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
- `USER_NOT_FOUND`: User with specified ID not found
- `TEAM_NOT_FOUND`: Team with specified ID not found
- `UNAUTHORIZED`: User not authorized to perform this action
- `FORBIDDEN`: User lacks required permissions
- `VALIDATION_ERROR`: Input data failed validation

## Deployment

### Environment Variables
```
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
JWT_SECRET=your_jwt_secret_key
FILE_STORAGE_PATH=/path/to/storage
```

### Health Check
```
GET /api/users/health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "ISO_timestamp",
  "service": "user-service"
}
```

## Testing

### Unit Tests
1. User profile CRUD operations
2. Team creation and management
3. Team membership handling
4. Input validation

### Integration Tests
1. End-to-end user profile management
2. Team creation and member management
3. Database interactions
4. Authentication flow integration

This documentation provides a comprehensive guide for implementing the User Service. For any questions or clarifications, please consult with the project team.