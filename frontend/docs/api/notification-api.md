# TaskFlow - Notification API

## Overview

The Notification API provides endpoints for managing user notifications, preferences, and real-time communication.

Base URL: `/api/notifications`

## Authentication

All endpoints require a valid JWT access token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### Get User Notifications
```
GET /api/notifications
```

Retrieves notifications for the authenticated user.

**Query Parameters:**
- `read`: Filter by read status (true/false)
- `type`: Filter by notification type (task, project, comment, mention)
- `limit`: Number of notifications to return (default: 20, max: 100)
- `page`: Page number (default: 1)

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
- 400: Validation error (invalid parameters)
- 401: Unauthorized (missing or invalid token)

### Get Unread Notification Count
```
GET /api/notifications/unread-count
```

Retrieves the count of unread notifications for the authenticated user.

**Response:**
```json
{
  "success": true,
  "data": {
    "count": "number"
  }
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)

### Mark Notification as Read
```
PUT /api/notifications/{id}/read
```

Marks a specific notification as read.

**Path Parameters:**
- `id`: Notification ID

**Response:**
```json
{
  "success": true,
  "message": "Notification marked as read"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not owner of notification)
- 404: Notification not found

### Mark All Notifications as Read
```
PUT /api/notifications/read-all
```

Marks all notifications for the authenticated user as read.

**Response:**
```json
{
  "success": true,
  "message": "All notifications marked as read",
  "data": {
    "count": "number"
  }
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)

### Delete Notification
```
DELETE /api/notifications/{id}
```

Deletes a specific notification.

**Path Parameters:**
- `id`: Notification ID

**Response:**
```json
{
  "success": true,
  "message": "Notification deleted"
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)
- 403: Forbidden (user not owner of notification)
- 404: Notification not found

### Delete All Read Notifications
```
DELETE /api/notifications/read
```

Deletes all read notifications for the authenticated user.

**Response:**
```json
{
  "success": true,
  "message": "Read notifications deleted",
  "data": {
    "count": "number"
  }
}
```

**Error Responses:**
- 401: Unauthorized (missing or invalid token)

### Get Notification Preferences
```
GET /api/notifications/preferences
```

Retrieves notification preferences for the authenticated user.

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

**Error Responses:**
- 401: Unauthorized (missing or invalid token)

### Update Notification Preferences
```
PUT /api/notifications/preferences
```

Updates notification preferences for the authenticated user.

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

**Error Responses:**
- 400: Validation error
- 401: Unauthorized (missing or invalid token)

## WebSocket API

### Connection
```
WebSocket connection to /ws/notifications
```

### Authentication
After establishing the WebSocket connection, clients must send an authentication message:

```json
{
  "event": "authenticate",
  "data": {
    "token": "jwt_access_token"
  }
}
```

### Incoming Events

#### Subscribe to Notifications
```json
{
  "event": "subscribe",
  "data": {}
}
```

#### Mark Notification as Read
```json
{
  "event": "mark_read",
  "data": {
    "id": "notification_id"
  }
}
```

### Outgoing Events

#### Authentication Success
```json
{
  "event": "authenticated",
  "data": {
    "userId": "string"
  }
}
```

#### Authentication Error
```json
{
  "event": "auth_error",
  "data": {
    "message": "string"
  }
}
```

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

#### Notification Deleted
```json
{
  "event": "notification_deleted",
  "data": {
    "id": "string"
  }
}
```

## Request Validation

### Notification Preferences Validation
- All preference values must be boolean
- Supported email preferences: `taskAssigned`, `taskCompleted`, `commentAdded`, `deadlineApproaching`
- Supported push preferences: `taskAssigned`, `taskCompleted`, `commentAdded`, `deadlineApproaching`

## Security Considerations

1. All WebSocket connections must be secured with JWT authentication
2. Users can only access their own notifications
3. Rate limiting is implemented for notification operations
4. Input validation is performed on all user-provided data

## Error Codes

| Code | Description |
|------|-------------|
| NOTIFICATION_NOT_FOUND | Notification with specified ID not found |
| UNAUTHORIZED | User not authorized to perform this action |
| FORBIDDEN | User lacks required permissions |
| VALIDATION_ERROR | Input data failed validation |
| WEBSOCKET_ERROR | WebSocket connection error |
| AUTHENTICATION_FAILED | WebSocket authentication failed |

This documentation provides detailed information about the Notification API endpoints. For implementation details, please refer to the Notification Service documentation.