# TaskFlow - API Documentation

This directory contains detailed API documentation for each service in the TaskFlow system.

## API Documentation by Service

1. [Auth API](./auth-api.md) - Authentication and user registration endpoints
2. [User API](./user-api.md) - User profile and team management endpoints
3. [Project API](./project-api.md) - Project creation and management endpoints
4. [Task API](./task-api.md) - Task creation and management endpoints
5. [Notification API](./notification-api.md) - Notification and real-time communication endpoints

## API Standards

All APIs in TaskFlow follow these standards:

### RESTful Principles
1. Use appropriate HTTP methods (GET, POST, PUT, DELETE)
2. Use plural nouns for resource names (e.g., /api/users, /api/projects)
3. Use HTTP status codes appropriately
4. Use consistent resource naming conventions

### Request/Response Format
1. All requests and responses use JSON format
2. UTF-8 encoding for all data
3. Consistent field naming (camelCase)
4. Proper error handling with standardized error responses

### Authentication
1. All endpoints (except auth endpoints) require JWT authentication
2. Tokens must be passed in the Authorization header:
   ```
   Authorization: Bearer <jwt_token>
   ```

### Pagination
For endpoints that return lists:
```
GET /api/resources?page=1&limit=20
```

Response format:
```json
{
  "success": true,
  "data": {
    "items": [...],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 100,
      "pages": 5
    }
  }
}
```

### Filtering and Sorting
Endpoints that support filtering and sorting:
```
GET /api/resources?status=active&sort=createdAt&order=desc
```

### Rate Limiting
All APIs implement rate limiting to prevent abuse:
- 100 requests per minute per IP for most endpoints
- 10 requests per minute per IP for authentication endpoints

## Common Response Formats

### Success Response
```json
{
  "success": true,
  "message": "Optional success message",
  "data": {
    // Response data
  }
}
```

### Error Response
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

### Validation Error Response
```json
{
  "success": false,
  "message": "Validation failed",
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {
      "field": "Error message for field"
    }
  }
}
```

## HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 422 | Unprocessable Entity |
| 429 | Too Many Requests |
| 500 | Internal Server Error |

For detailed information about each service's API endpoints, please refer to the individual API documentation files.