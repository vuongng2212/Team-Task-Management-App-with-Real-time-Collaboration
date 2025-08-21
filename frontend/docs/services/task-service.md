# TaskFlow - Task Service

## Overview

The Task Service manages tasks, task assignments, comments, and task-related data. It handles task creation, status updates, assignments, and collaboration features.

## Key Responsibilities

1. Task creation and management
2. Task assignment and tracking
3. Task status and priority management
4. Task comments and collaboration
5. Task search and filtering
6. Due date tracking and reminders

## API Endpoints

### Create Task
```
POST /api/tasks
```

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

### Get Task
```
GET /api/tasks/:id
```

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

### Update Task
```
PUT /api/tasks/:id
```

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

### Delete Task
```
DELETE /api/tasks/:id
```

**Response:**
```json
{
  "success": true,
  "message": "Task deleted successfully"
}
```

### Get Project Tasks
```
GET /api/projects/:id/tasks
```

**Query Parameters:**
- `status`: Filter by status (todo, in-progress, review, done)
- `priority`: Filter by priority (low, medium, high)
- `assigneeId`: Filter by assignee

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
    ]
  }
}
```

### Add Task Comment
```
POST /api/tasks/:id/comments
```

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

### Update Task Comment
```
PUT /api/tasks/:taskId/comments/:commentId
```

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

### Delete Task Comment
```
DELETE /api/tasks/:taskId/comments/:commentId
```

**Response:**
```json
{
  "success": true,
  "message": "Comment deleted successfully"
}
```

### Search Tasks
```
GET /api/tasks?query=:searchTerm
```

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
    ]
  }
}
```

## Data Models

### Task
```sql
CREATE TABLE tasks (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(255) NOT NULL,
  description TEXT,
  project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
  status VARCHAR(50) NOT NULL DEFAULT 'todo',     -- todo, in-progress, review, done
  priority VARCHAR(50) NOT NULL DEFAULT 'medium', -- low, medium, high
  assignee_id UUID REFERENCES users(id) ON DELETE SET NULL,
  due_date TIMESTAMP WITH TIME ZONE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### Task Comment
```sql
CREATE TABLE task_comments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  content TEXT NOT NULL,
  task_id UUID REFERENCES tasks(id) ON DELETE CASCADE,
  author_id UUID REFERENCES users(id) ON DELETE CASCADE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### Task Assignment
```sql
CREATE TABLE task_assignments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  task_id UUID REFERENCES tasks(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  assigned_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  assigned_by UUID REFERENCES users(id),
  UNIQUE(task_id, user_id)
);
```

## Business Logic

### Task Management
1. Only project members can create tasks for that project
2. Only task assignees or project owners can update/delete tasks
3. Task titles must be unique within a project
4. Tasks can be moved between status values (todo → in-progress → review → done)

### Task Assignment
1. Tasks can be assigned to multiple users
2. Users can be assigned to multiple tasks
3. Assignment notifications should be sent to assignees
4. Unassignment should remove user from task

### Task Comments
1. Only project members can comment on tasks
2. Users can only edit/delete their own comments
3. Comment notifications should be sent to relevant users

### Task Status Tracking
1. Status changes should be logged for audit purposes
2. Progress tracking should be calculated based on status distribution
3. Due date reminders should be sent to assignees

## Security Considerations

1. All endpoints must be protected with JWT authentication
2. Users can only access tasks from projects they are members of
3. Task assignment must be verified for task-related operations
4. Input validation must be performed on all user-provided data
5. Proper authorization checks must be implemented for create/update/delete operations

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
- `TASK_NOT_FOUND`: Task with specified ID not found
- `UNAUTHORIZED`: User not authorized to perform this action
- `FORBIDDEN`: User lacks required permissions
- `VALIDATION_ERROR`: Input data failed validation
- `TASK_ALREADY_ASSIGNED`: Task is already assigned to this user

## Deployment

### Environment Variables
```
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
JWT_SECRET=your_jwt_secret_key
NOTIFICATION_SERVICE_URL=http://notification-service:3000
```

### Health Check
```
GET /api/tasks/health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "ISO_timestamp",
  "service": "task-service"
}
```

## Testing

### Unit Tests
1. Task CRUD operations
2. Task assignment and status management
3. Comment functionality
4. Input validation

### Integration Tests
1. End-to-end task management
2. Project and task relationship management
3. Database interactions
4. Authentication flow integration
5. Notification service integration

This documentation provides a comprehensive guide for implementing the Task Service. For any questions or clarifications, please consult with the project team.