# TaskFlow - Project Service

## Overview

The Project Service manages projects, project memberships, and project-related information. It handles project creation, team assignments, and project metadata.

## Key Responsibilities

1. Project creation and management
2. Project membership handling
3. Project metadata management
4. Project search and filtering
5. Project status tracking
6. Project archiving and deletion

## API Endpoints

### Create Project
```
POST /api/projects
```

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

### Get Project
```
GET /api/projects/:id
```

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

### Update Project
```
PUT /api/projects/:id
```

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

### Delete Project
```
DELETE /api/projects/:id
```

**Response:**
```json
{
  "success": true,
  "message": "Project deleted successfully"
}
```

### Get Team Projects
```
GET /api/teams/:id/projects
```

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
    ]
  }
}
```

### Add Project Member
```
POST /api/projects/:id/members
```

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

### Remove Project Member
```
DELETE /api/projects/:id/members/:userId
```

**Response:**
```json
{
  "success": true,
  "message": "Member removed from project successfully"
}
```

### Search Projects
```
GET /api/projects?query=:searchTerm
```

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
        "status": "string",
        "progress": "number"
      }
    ]
  }
}
```

## Data Models

### Project
```sql
CREATE TABLE projects (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  description TEXT,
  team_id UUID REFERENCES teams(id) ON DELETE CASCADE,
  status VARCHAR(50) NOT NULL DEFAULT 'active',  -- active, archived
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### Project Membership
```sql
CREATE TABLE project_members (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(50) NOT NULL DEFAULT 'member',  -- owner, member
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(project_id, user_id)
);
```

## Business Logic

### Project Management
1. Only team members can create projects for that team
2. Only project owners can delete projects
3. Project names must be unique within a team
4. Projects can be archived but not permanently deleted

### Project Membership
1. Project owners can add/remove members
2. Users can be members of multiple projects
3. A project must have at least one owner
4. Ownership transfer must be handled when owners leave

### Project Status
1. Active projects are visible to all members
2. Archived projects are read-only
3. Progress tracking is calculated based on task completion

## Security Considerations

1. All endpoints must be protected with JWT authentication
2. Users can only access projects they are members of
3. Project membership must be verified for project-related operations
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
- `PROJECT_NOT_FOUND`: Project with specified ID not found
- `UNAUTHORIZED`: User not authorized to perform this action
- `FORBIDDEN`: User lacks required permissions
- `VALIDATION_ERROR`: Input data failed validation
- `PROJECT_ALREADY_EXISTS`: Project with this name already exists in the team

## Deployment

### Environment Variables
```
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
JWT_SECRET=your_jwt_secret_key
```

### Health Check
```
GET /api/projects/health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "ISO_timestamp",
  "service": "project-service"
}
```

## Testing

### Unit Tests
1. Project CRUD operations
2. Project membership handling
3. Input validation
4. Status tracking calculations

### Integration Tests
1. End-to-end project management
2. Team and project relationship management
3. Database interactions
4. Authentication flow integration

This documentation provides a comprehensive guide for implementing the Project Service. For any questions or clarifications, please consult with the project team.