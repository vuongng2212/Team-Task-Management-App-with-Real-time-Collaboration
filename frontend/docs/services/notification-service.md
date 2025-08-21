# TaskFlow - Notification Service

## Overview

The Notification Service handles real-time notifications, messaging, and alerting for the TaskFlow application. It manages in-app notifications, email notifications, and WebSocket connections for real-time updates.

## Key Responsibilities

1. Real-time notification delivery via WebSocket
2. Email notification sending
3. Notification persistence and management
4. User notification preferences
5. Notification templates and content management
6. Push notification integration (future enhancement)

## API Endpoints

### Get User Notifications
```
GET /api/notifications
```

**Query Parameters:**
- `read`: Filter by read status (true/false)
- `type`: Filter by notification type
- `limit`: Number of notifications to return (default: 20)

**Response:**
```json
{
  "success": true,
  "data": {
    "notifications": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "type": "string",
        "read": "boolean",
        "createdAt": "timestamp"
      }
    ]
  }
}
```

### Mark Notification as Read
```
PUT /api/notifications/{id}/read
```

**Path Parameters:**
- `id`: Notification ID

**Response:**
```json
{
  "success": true,
  "message": "Notification marked as read"
}
```

### Mark All Notifications as Read
```
PUT /api/notifications/read-all
```

**Response:**
```json
{
  "success": true,
  "message": "All notifications marked as read"
}
```

### Delete Notification
```
DELETE /api/notifications/{id}
```

**Path Parameters:**
- `id`: Notification ID

**Response:**
```json
{
  "success": true,
  "message": "Notification deleted"
}
```

### Get Notification Preferences
```
GET /api/notifications/preferences
```

**Response:**
```json
{
  "success": true,
  "data": {
    "preferences": {
      "email": {
        "taskAssigned": true,
        "taskCompleted": true,
        "commentAdded": true,
        "deadlineApproaching": true
      },
      "push": {
        "taskAssigned": true,
        "taskCompleted": false,
        "commentAdded": true,
        "deadlineApproaching": true
      }
    }
  }
}
```

### Update Notification Preferences
```
PUT /api/notifications/preferences
```

**Request Body:**
```json
{
  "email": {
    "taskAssigned": true,
    "taskCompleted": true,
    "commentAdded": true,
    "deadlineApproaching": true
  },
  "push": {
    "taskAssigned": true,
    "taskCompleted": false,
    "commentAdded": true,
    "deadlineApproaching": true
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Notification preferences updated"
}
```

## WebSocket Events

### Connection
```
WebSocket connection to /ws/notifications
```

### Incoming Events

#### Subscribe to Notifications
```json
{
  "event": "subscribe",
  "data": {
    "userId": "string"
  }
}
```

### Outgoing Events

#### New Notification
```json
{
  "event": "notification",
  "data": {
    "id": "string",
    "title": "string",
    "description": "string",
    "type": "string",
    "createdAt": "timestamp"
  }
}
```

#### Notification Read
```json
{
  "event": "notification_read",
  "data": {
    "id": "string"
  }
}
```

## Data Models

### Notification
```sql
CREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  type VARCHAR(50) NOT NULL,  -- task, project, comment, mention
  read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### Notification Preferences
```sql
CREATE TABLE notification_preferences (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  email_task_assigned BOOLEAN DEFAULT TRUE,
  email_task_completed BOOLEAN DEFAULT TRUE,
  email_comment_added BOOLEAN DEFAULT TRUE,
  email_deadline_approaching BOOLEAN DEFAULT TRUE,
  push_task_assigned BOOLEAN DEFAULT TRUE,
  push_task_completed BOOLEAN DEFAULT FALSE,
  push_comment_added BOOLEAN DEFAULT TRUE,
  push_deadline_approaching BOOLEAN DEFAULT TRUE,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(user_id)
);
```

### User Sessions (for WebSocket)
```sql
CREATE TABLE user_sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  socket_id VARCHAR(255) NOT NULL,
  connected_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(socket_id)
);
```

## Business Logic

### Notification Types
1. **Task Assigned**: When a user is assigned to a task
2. **Task Completed**: When a task is marked as completed
3. **Comment Added**: When a comment is added to a task or project
4. **Mention**: When a user is mentioned in a comment
5. **Deadline Approaching**: When a task deadline is approaching
6. **Project Update**: When a project is updated

### Notification Delivery
1. Real-time delivery via WebSocket for active users
2. Email delivery based on user preferences
3. Push notifications for mobile devices (future enhancement)

### Notification Preferences
1. Users can customize which notifications they receive via email
2. Users can customize which notifications they receive via push
3. Preferences are stored per user and applied to all notifications

### Notification Retention
1. Read notifications are retained for 30 days
2. Unread notifications are retained indefinitely
3. Users can manually delete notifications

## Security Considerations

1. All WebSocket connections must be secured with JWT authentication
2. Users can only access their own notifications
3. Notification preferences are encrypted at rest
4. Email addresses are validated before sending notifications
5. Rate limiting is implemented for notification sending

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
- `NOTIFICATION_NOT_FOUND`: Notification with specified ID not found
- `UNAUTHORIZED`: User not authorized to perform this action
- `FORBIDDEN`: User lacks required permissions
- `VALIDATION_ERROR`: Input data failed validation
- `WEBSOCKET_ERROR`: WebSocket connection error

## Deployment

### Environment Variables
```
DATABASE_URL=postgresql://user:password@host:port/database
REDIS_URL=redis://host:port
JWT_SECRET=your_jwt_secret_key
EMAIL_SERVICE_PROVIDER=smtp
EMAIL_SERVICE_URL=smtp://host:port
EMAIL_SERVICE_USER=username
EMAIL_SERVICE_PASSWORD=password
```

### Health Check
```
GET /api/notifications/health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "ISO_timestamp",
  "service": "notification-service"
}
```

## Testing

### Unit Tests
1. Notification creation and delivery
2. WebSocket connection handling
3. Email notification sending
4. Notification preference management

### Integration Tests
1. End-to-end notification flow
2. WebSocket real-time delivery
3. Database interactions
4. Email service integration

This documentation provides a comprehensive guide for implementing the Notification Service. For any questions or clarifications, please consult with the project team.