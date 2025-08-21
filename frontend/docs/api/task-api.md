# TaskFlow - Task API

## Overview

The Task API provides endpoints for managing tasks, task assignments, comments, and task-related data.

Base URL: `/api/tasks`

## Authentication

All endpoints require a valid JWT access token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### Create Task
```
POST /api/tasks
```

Creates a new task.

**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "projectId": "string",
  "status": "string",      // todo, in-progress, review, done
  "priority": "string",    // low, medium, high
  "assigneeId": "string",
  "dueDate": "timestamp"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "task": {
      "id": "string",
      "title": "string",
      "description": "string",
      "projectId": "string",
      "status": "string",
      "priority": "string",
      "assigneeId": "string",
      "dueDate": "timestamp",
      "createdAt": "timestamp",
      "updatedAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not a member of the specified project)
- 404: Project not found
- 409: Task with this title already exists in the project

### Get Task
```
GET /api/tasks/{id}
```

Retrieves details of a specific task.

**Path Parameters:**
- `id`: Task ID

**Response:**
```json
{
  "success": true,
  "data": {
    "task": {
      "id": "string",
      "title": "string",
      "description": "string",
      "projectId": "string",
      "status": "string",
      "priority": "string",
      "assigneeId": "string",
      "assigneeName": "string",
      "dueDate": "timestamp",
      "createdAt": "timestamp",
      "updatedAt": "timestamp",
      "comments": [
        {
          "id": "string",
          "content": "string",
          "authorId": "string",
          "authorName": "string",
          "createdAt": "timestamp"
        }
      ]
    }
  }
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not a member of this task's project)
- 404: Task not found

### Update Task
```
PUT /api/tasks/{id}
```

Updates a task's information.

**Path Parameters:**
- `id`: Task ID

**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "status": "string",
  "priority": "string",
  "assigneeId": "string",
  "dueDate": "timestamp"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Task updated successfully",
  "data": {
    "task": {
      "id": "string",
      "title": "string",
      "description": "string",
      "projectId": "string",
      "status": "string",
      "priority": "string",
      "assigneeId": "string",
      "dueDate": "timestamp",
      "updatedAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not assigned to task or project owner)
- 404: Task not found
- 409: Task with this title already exists in the project

### Delete Task
```
DELETE /api/tasks/{id}
```

Deletes a task.

**Path Parameters:**
- `id`: Task ID

**Response:**
```json
{
  "success": true,
  "message": "Task deleted successfully"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not assigned to task or project owner)
- 404: Task not found

### Get Project Tasks
```
GET /api/projects/{id}/tasks
```

Retrieves all tasks for a specific project.

**Path Parameters:**
- `id`: Project ID

**Query Parameters:**
- `status`: Filter by status (todo, in-progress, review, done)
- `priority`: Filter by priority (low, medium, high)
- `assigneeId`: Filter by assignee
- `page`: Page number (default: 1)
- `limit`: Results per page (default: 20, max: 100)

**Response:**
```json
{
  "success": true,
  "data": {
    "tasks": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "status": "string",
        "priority": "string",
        "assigneeId": "string",
        "assigneeName": "string",
        "dueDate": "timestamp",
        "createdAt": "timestamp"
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
- 403: Forbidden (user not a member of this project)
- 404: Project not found

### Search Tasks
```
GET /api/tasks
```

Searches for tasks by title or description.

**Query Parameters:**
- `query`: Search term
- `projectId`: Filter by project (optional)
- `status`: Filter by status (todo, in-progress, review, done)
- `priority`: Filter by priority (low, medium, high)
- `assigneeId`: Filter by assignee
- `page`: Page number (default: 1)
- `limit`: Results per page (default: 20, max: 100)

**Response:**
```json
{
  "success": true,
  "data": {
    "tasks": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "projectId": "string",
        "projectName": "string",
        "status": "string",
        "priority": "string",
        "assigneeId": "string",
        "assigneeName": "string",
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

### Add Task Comment
```
POST /api/tasks/{id}/comments
```

Adds a comment to a task.

**Path Parameters:**
- `id`: Task ID

**Request Body:**
```json
{
  "content": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Comment added successfully",
  "data": {
    "comment": {
      "id": "string",
      "content": "string",
      "authorId": "string",
      "taskId": "string",
      "createdAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not a member of task's project)
- 404: Task not found

### Update Task Comment
```
PUT /api/tasks/{taskId}/comments/{commentId}
```

Updates a task comment.

**Path Parameters:**
- `taskId`: Task ID
- `commentId`: Comment ID

**Request Body:**
```json
{
  "content": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Comment updated successfully",
  "data": {
    "comment": {
      "id": "string",
      "content": "string",
      "authorId": "string",
      "taskId": "string",
      "updatedAt": "timestamp"
    }
  }
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not author of comment)
- 404: Task or comment not found

### Delete Task Comment
```
DELETE /api/tasks/{taskId}/comments/{commentId}
```

Deletes a task comment.

**Path Parameters:**
- `taskId`: Task ID
- `commentId`: Comment ID

**Response:**
```json
{
  "success": true,
  "message": "Comment deleted successfully"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not author of comment or project owner)
- 404: Task or comment not found

### Get Task Comments
```
GET /api/tasks/{id}/comments
```

Retrieves all comments for a specific task.

**Path Parameters:**
- `id`: Task ID

**Query Parameters:**
- `page`: Page number (default: 1)
- `limit`: Results per page (default: 20, max: 100)

**Response:**
```json
{
  "success": true,
  "data": {
    "comments": [
      {
        "id": "string",
        "content": "string",
        "authorId": "string",
        "authorName": "string",
        "authorAvatar": "string",
        "createdAt": "timestamp"
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
- 403: Forbidden (user not a member of task's project)
- 404: Task not found

### Assign Task
```
POST /api/tasks/{id}/assign
```

Assigns a task to a user.

**Path Parameters:**
- `id`: Task ID

**Request Body:**
```json
{
  "userId": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Task assigned successfully"
}
```

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not a member of task's project)
- 404: Task or user not found
- 409: Task already assigned to this user

### Unassign Task
```
DELETE /api/tasks/{id}/assign/{userId}
```

Unassigns a task from a user.

**Path Parameters:**
- `id`: Task ID
- `userId`: User ID

**Response:**
```json
{
  "success": true,
  "message": "Task unassigned successfully"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not assigned to task or project owner)
- 404: Task or user not found

## Request Validation

### Task Creation Validation
- `title`: Required, 1-255 characters, unique within project
- `description`: Optional, max 2000 characters
- `projectId`: Required, valid UUID
- `status`: Optional, must be "todo", "in-progress", "review", or "done"
- `priority`: Optional, must be "low", "medium", or "high"
- `assigneeId`: Optional, valid UUID
- `dueDate`: Optional, valid timestamp

### Task Update Validation
- `title`: Optional, 1-255 characters, unique within project
- `description`: Optional, max 2000 characters
- `status`: Optional, must be "todo", "in-progress", "review", or "done"
- `priority`: Optional, must be "low", "medium", or "high"
- `assigneeId`: Optional, valid UUID
- `dueDate`: Optional, valid timestamp

### Task Comment Validation
- `content`: Required, 1-2000 characters

### Task Assignment Validation
- `userId`: Required, valid UUID

## Security Considerations

1. Only project members can create tasks for that project
2. Only task assignees or project owners can update/delete tasks
3. Only project members can comment on tasks
4. Users can only edit/delete their own comments
5. Task assignment must be verified for task-related operations
6. Input validation must be performed on all user-provided data
7. Proper authorization checks must be implemented for create/update/delete operations

## Error Codes

| Code | Description |
|------|-------------|
| TASK_NOT_FOUND | Task with specified ID not found |
| PROJECT_NOT_FOUND | Project with specified ID not found |
| USER_NOT_FOUND | User with specified ID not found |
| COMMENT_NOT_FOUND | Comment with specified ID not found |
| UNAUTHORIZED | User not authorized to perform this action |
| FORBIDDEN | User lacks required permissions |
| VALIDATION_ERROR | Input data failed validation |
| TASK_ALREADY_EXISTS | Task with this title already exists in the project |
| TASK_ALREADY_ASSIGNED | Task is already assigned to this user |
| COMMENT_TOO_LONG | Comment exceeds maximum length |

This documentation provides detailed information about the Task API endpoints. For implementation details, please refer to the Task Service documentation.