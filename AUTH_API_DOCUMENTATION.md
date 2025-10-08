# Authentication API Documentation

## Overview
This document describes the authentication API endpoints for the Tara Academy API.

## Base URL
```
/api/v1/auth
```

## Authentication
All authentication endpoints are public and do not require authentication.

## API Endpoints

### 1. Login
**POST** `/api/v1/auth/login`

Authenticates a user and returns a JWT token.

**Request Body**:
```json
{
  "usernameOrEmail": "user@example.com",
  "password": "password123"
}
```

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Login successful",
  "error": null,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "email": "user@example.com",
    "username": "user123",
    "fullName": "John Doe",
    "role": "ADMIN",
    "roles": ["ROLE_ADMIN"],
    "expiresIn": 86400
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

**Error Responses**:

**401 Unauthorized - Invalid credentials**:
```json
{
  "status": 401,
  "message": "Invalid credentials",
  "error": "User not found",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

**401 Unauthorized - Incorrect password**:
```json
{
  "status": 401,
  "message": "Invalid credentials",
  "error": "Incorrect password",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

**401 Unauthorized - Account inactive**:
```json
{
  "status": 401,
  "message": "Account is inactive",
  "error": "User account is disabled",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

**401 Unauthorized - Account deleted**:
```json
{
  "status": 401,
  "message": "Account not found",
  "error": "User account has been deleted",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 2. Logout
**POST** `/api/v1/auth/logout`

Logs out the current user (client-side token removal).

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Logout successful",
  "error": null,
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 3. Get Current User
**GET** `/api/v1/auth/me`

Returns information about the currently authenticated user.

**Authorization**: Bearer token required

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "User found",
  "error": null,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "user123",
    "email": "user@example.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "customerCode": "CUST001",
    "accountBalance": 1000.00,
    "avt": "https://example.com/avatar.jpg",
    "role": "ADMIN",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z",
    "status": 1,
    "isDeleted": 0,
    "password": null
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

**Error Responses**:

**401 Unauthorized - No token**:
```json
{
  "status": 401,
  "message": "Not authenticated",
  "error": "No valid token found",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

**404 Not Found - User not found**:
```json
{
  "status": 404,
  "message": "User not found",
  "error": "User with email user@example.com not found",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Validation Rules

### LoginRequest
- `usernameOrEmail`: Required, cannot be blank
- `password`: Required, cannot be blank

## Authentication Flow

1. **Login**: User sends credentials to `/api/v1/auth/login`
2. **Token Generation**: Server validates credentials and generates JWT token
3. **Token Usage**: Client includes token in Authorization header for protected endpoints
4. **Token Format**: `Authorization: Bearer <token>`

## JWT Token Details

- **Algorithm**: HS256
- **Expiration**: 24 hours
- **Claims**: 
  - `sub`: User email
  - `id`: User ID
  - `scope`: User roles
  - `userAgent`: Client user agent
- **Issuer**: "BlogDemo"

## User Roles

- `ROLE_USER`: Basic user role
- `ROLE_ADMIN`: Administrator role
- `ROLE_TEACHER`: Teacher role
- `ROLE_STUDENT`: Student role

## Security Features

- **Password Hashing**: BCrypt encryption
- **Account Status**: Checks for active accounts (status = 1)
- **Account Deletion**: Checks for deleted accounts (isDeleted = 0)
- **User Agent Tracking**: Logs client user agent for security
- **Token Expiration**: Automatic token expiration after 24 hours

## Example Usage

### Login:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin@example.com",
    "password": "admin123"
  }'
```

### Get Current User:
```bash
curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Logout:
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Error Handling

All endpoints return consistent error responses with:
- `status`: HTTP status code
- `message`: Human-readable message
- `error`: Detailed error information
- `data`: Response data (null for errors)
- `timestamp`: Response timestamp

## Notes

- Login supports both username and email authentication
- Passwords are case-sensitive
- JWT tokens are stateless and do not require server-side storage
- Logout is primarily handled client-side by removing the token
- All authentication endpoints are public and do not require authentication
