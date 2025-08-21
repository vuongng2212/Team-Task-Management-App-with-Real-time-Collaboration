# TaskFlow - User API

## Overview

The User API provides endpoints for managing user profiles, teams, and user-related data.

Base URL: `/api/users`

## Authentication

All endpoints require a valid JWT access token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### Get Current User Profile
```
GET /api/users/me
```

Retrieves the profile of the authenticated user.

**Response:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "string",
      "name": "string",
      "email": "string",
      "avatar": "string",
      "bio": "string",
      "createdAt": "timestamp",
      "updatedAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)

### Get User Profile
```
GET /api/users/{id}
```

Retrieves the profile of a specific user.

**Path Parameters:**
- `id`: User ID

**Response:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "string",
      "name": "string",
      "email": "string",
      "avatar": "string",
      "bio": "string",
      "createdAt": "timestamp",
      "updatedAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 404: User not found

### Update User Profile
```
PUT /api/users/{id}
```

Updates a user's profile information.

**Path Parameters:**
- `id`: User ID

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

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user can only update their own profile)
- 404: User not found

### Search Users
```
GET /api/users
```

Searches for users by name or email.

**Query Parameters:**
- `query`: Search term
- `page`: Page number (default: 1)
- `limit`: Results per page (default: 20, max: 100)

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
    ],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 100,
      "pages": 5
    }
  }
}
```

**Error Responses:**
- 400: Validation error (invalid pagination parameters)
- 401: Unauthorized (missing or invalid token)

### Create Team
```
POST /api/teams
```

Creates a new team.

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

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 409: Team with this name already exists

### Get Team Details
```
GET /api/teams/{id}
```

Retrieves details of a specific team.

**Path Parameters:**
- `id`: Team ID

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

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not a member of this team)
- 404: Team not found

### Update Team
```
PUT /api/teams/{id}
```

Updates a team's information.

**Path Parameters:**
- `id`: Team ID

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
  "message": "Team updated successfully",
  "data": {
    "team": {
      "id": "string",
      "name": "string",
      "description": "string",
      "updatedAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only team admins can update team)
- 404: Team not found
- 409: Team with this name already exists

### Delete Team
```
DELETE /api/teams/{id}
```

Deletes a team and all associated data.

**Path Parameters:**
- `id`: Team ID

**Response:**
```json
{
  "success": true,
  "message": "Team deleted successfully"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only team admins can delete team)
- 404: Team not found

### Get User Teams
```
GET /api/users/{id}/teams
```

Retrieves all teams that a user is a member of.

**Path Parameters:**
- `id`: User ID

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

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (users can only view their own teams)
- 404: User not found

### Get Team Members
```
GET /api/teams/{id}/members
```

Retrieves all members of a team.

**Path Parameters:**
- `id`: Team ID

**Response:**
```json
{
  "success": true,
  "data": {
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
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not a member of this team)
- 404: Team not found

### Add Team Member
```
POST /api/teams/{id}/members
```

Adds a user to a team.

**Path Parameters:**
- `id`: Team ID

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

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only team admins can add members)
- 404: Team or user not found
- 409: User is already a member of this team

### Update Team Member Role
```
PUT /api/teams/{id}/members/{userId}
```

Updates a team member's role.

**Path Parameters:**
- `id`: Team ID
- `userId`: User ID

**Request Body:**
```json
{
  "role": "string"  // admin, member
}
```

**Response:**
```json
{
  "success": true,
  "message": "Member role updated successfully"
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only team admins can update member roles)
- 404: Team or user not found

### Remove Team Member
```
DELETE /api/teams/{id}/members/{userId}
```

Removes a user from a team.

**Path Parameters:**
- `id`: Team ID
- `userId`: User ID

**Response:**
```json
{
  "success": true,
  "message": "Member removed from team successfully"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only team admins can remove members, admins cannot remove themselves)
- 404: Team or user not found

## Request Validation

### User Profile Update Validation
- `name`: Optional, 1-255 characters
- `bio`: Optional, max 1000 characters
- `avatar`: Optional, valid URL

### Team Creation Validation
- `name`: Required, 1-255 characters, unique within user's teams
- `description`: Optional, max 1000 characters

### Team Update Validation
- `name`: Optional, 1-255 characters, unique within user's teams
- `description`: Optional, max 1000 characters

### Add Team Member Validation
- `userId`: Required, valid UUID
- `role`: Required, must be "admin" or "member"

### Update Team Member Role Validation
- `role`: Required, must be "admin" or "member"

## Security Considerations

1. Users can only update their own profiles
2. Team operations require appropriate permissions (admin vs member)
3. Team membership must be verified for team-related operations
4. Input validation must be performed on all user-provided data
5. File uploads (avatars) must be properly sanitized and validated

## Error Codes

| Code | Description |
|------|-------------|
| USER_NOT_FOUND | User with specified ID not found |
| TEAM_NOT_FOUND | Team with specified ID not found |
| UNAUTHORIZED | User not authorized to perform this action |
| FORBIDDEN | User lacks required permissions |
| VALIDATION_ERROR | Input data failed validation |
| TEAM_ALREADY_EXISTS | Team with this name already exists |
| USER_ALREADY_MEMBER | User is already a member of this team |
| CANNOT_REMOVE_SELF | Admins cannot remove themselves from team |

This documentation provides detailed information about the User API endpoints. For implementation details, please refer to the User Service documentation.