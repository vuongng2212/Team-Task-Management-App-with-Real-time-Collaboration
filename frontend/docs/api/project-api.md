# TaskFlow - Project API

## Overview

The Project API provides endpoints for managing projects, project memberships, and project-related data.

Base URL: `/api/projects`

## Authentication

All endpoints require a valid JWT access token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### Create Project
```
POST /api/projects
```

Creates a new project.

**Request Body:**
```json
{
  "name": "string",
  "description": "string",
  "teamId": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Project created successfully",
  "data": {
    "project": {
      "id": "string",
      "name": "string",
      "description": "string",
      "teamId": "string",
      "status": "active",
      "createdAt": "timestamp",
      "updatedAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not a member of the specified team)
- 409: Project with this name already exists in the team

### Get Project
```
GET /api/projects/{id}
```

Retrieves details of a specific project.

**Path Parameters:**
- `id`: Project ID

**Response:**
```json
{
  "success": true,
  "data": {
    "project": {
      "id": "string",
      "name": "string",
      "description": "string",
      "teamId": "string",
      "status": "string",
      "createdAt": "timestamp",
      "updatedAt": "timestamp",
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
- 403: Forbidden (user not a member of this project)
- 404: Project not found

### Update Project
```
PUT /api/projects/{id}
```

Updates a project's information.

**Path Parameters:**
- `id`: Project ID

**Request Body:**
```json
{
  "name": "string",
  "description": "string",
  "status": "string"  // active, archived
}
```

**Response:**
```json
{
  "success": true,
  "message": "Project updated successfully",
  "data": {
    "project": {
      "id": "string",
      "name": "string",
      "description": "string",
      "teamId": "string",
      "status": "string",
      "updatedAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only project owners can update project)
- 404: Project not found
- 409: Project with this name already exists in the team

### Delete Project
```
DELETE /api/projects/{id}
```

Deletes a project and all associated data.

**Path Parameters:**
- `id`: Project ID

**Response:**
```json
{
  "success": true,
  "message": "Project deleted successfully"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only project owners can delete project)
- 404: Project not found

### Get Team Projects
```
GET /api/teams/{id}/projects
```

Retrieves all projects for a specific team.

**Path Parameters:**
- `id`: Team ID

**Query Parameters:**
- `status`: Filter by status (active, archived)
- `page`: Page number (default: 1)
- `limit`: Results per page (default: 20, max: 100)

**Response:**
```json
{
  "success": true,
  "data": {
    "projects": [
      {
        "id": "string",
        "name": "string",
        "description": "string",
        "status": "string",
        "progress": "number",
        "tasksCount": "number",
        "completedTasks": "number",
        "dueDate": "timestamp"
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
- 403: Forbidden (user not a member of this team)
- 404: Team not found

### Search Projects
```
GET /api/projects
```

Searches for projects by name or description.

**Query Parameters:**
- `query`: Search term
- `teamId`: Filter by team (optional)
- `status`: Filter by status (active, archived)
- `page`: Page number (default: 1)
- `limit`: Results per page (default: 20, max: 100)

**Response:**
```json
{
  "success": true,
  "data": {
    "projects": [
      {
        "id": "string",
        "name": "string",
        "description": "string",
        "teamId": "string",
        "teamName": "string",
        "status": "string",
        "progress": "number"
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

### Add Project Member
```
POST /api/projects/{id}/members
```

Adds a user to a project.

**Path Parameters:**
- `id`: Project ID

**Request Body:**
```json
{
  "userId": "string",
  "role": "string"  // owner, member
}
```

**Response:**
```json
{
  "success": true,
  "message": "Member added to project successfully"
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only project owners can add members)
- 404: Project or user not found
- 409: User is already a member of this project

### Update Project Member Role
```
PUT /api/projects/{id}/members/{userId}
```

Updates a project member's role.

**Path Parameters:**
- `id`: Project ID
- `userId`: User ID

**Request Body:**
```json
{
  "role": "string"  // owner, member
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
- 403: Forbidden (only project owners can update member roles)
- 404: Project or user not found

### Remove Project Member
```
DELETE /api/projects/{id}/members/{userId}
```

Removes a user from a project.

**Path Parameters:**
- `id`: Project ID
- `userId`: User ID

**Response:**
```json
{
  "success": true,
  "message": "Member removed from project successfully"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (only project owners can remove members, owners cannot remove themselves)
- 404: Project or user not found

### Get Project Members
```
GET /api/projects/{id}/members
```

Retrieves all members of a project.

**Path Parameters:**
- `id`: Project ID

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
- 403: Forbidden (user not a member of this project)
- 404: Project not found

## Request Validation

### Project Creation Validation
- `name`: Required, 1-255 characters, unique within team
- `description`: Optional, max 1000 characters
- `teamId`: Required, valid UUID

### Project Update Validation
- `name`: Optional, 1-255 characters, unique within team
- `description`: Optional, max 1000 characters
- `status`: Optional, must be "active" or "archived"

### Add Project Member Validation
- `userId`: Required, valid UUID
- `role`: Required, must be "owner" or "member"

### Update Project Member Role Validation
- `role`: Required, must be "owner" or "member"

## Security Considerations

1. Only team members can create projects for that team
2. Only project owners can delete projects
3. Project membership must be verified for project-related operations
4. Input validation must be performed on all user-provided data
5. Proper authorization checks must be implemented for create/update/delete operations

## Error Codes

| Code | Description |
|------|-------------|
| PROJECT_NOT_FOUND | Project with specified ID not found |
| TEAM_NOT_FOUND | Team with specified ID not found |
| USER_NOT_FOUND | User with specified ID not found |
| UNAUTHORIZED | User not authorized to perform this action |
| FORBIDDEN | User lacks required permissions |
| VALIDATION_ERROR | Input data failed validation |
| PROJECT_ALREADY_EXISTS | Project with this name already exists in the team |
| USER_ALREADY_MEMBER | User is already a member of this project |
| CANNOT_REMOVE_SELF | Owners cannot remove themselves from project |

This documentation provides detailed information about the Project API endpoints. For implementation details, please refer to the Project Service documentation.