# Career API Documentation

## Overview
This document describes the CRUD API endpoints for managing Career entities in the Tara Academy API.

## Base URL
```
/api/v1/careers
```

## Authentication
- **Public endpoints**: GET operations (view careers)
- **Admin endpoints**: POST, PUT, DELETE, PATCH operations (manage careers)
- Admin endpoints require `ADMIN` role authentication

## API Endpoints

### 1. Create Career
**POST** `/api/v1/careers`

Creates a new career.

**Authorization**: Admin only

**Request Body**:
```json
{
  "title": "Software Engineer",
  "description": "Develop and maintain software applications",
  "status": 1
}
```

**Response** (201 Created):
```json
{
  "status": 201,
  "message": "Career created successfully",
  "error": null,
  "data": {
    "id": 1,
    "title": "Software Engineer",
    "description": "Develop and maintain software applications",
    "createdById": null,
    "createdByUsername": null,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z",
    "status": 1,
    "isDeleted": 0
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 2. Get Career by ID
**GET** `/api/v1/careers/{id}`

Retrieves a specific career by its ID.

**Authorization**: Public

**Path Parameters**:
- `id` (integer): Career ID

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Career found",
  "error": null,
  "data": {
    "id": 1,
    "title": "Software Engineer",
    "description": "Develop and maintain software applications",
    "createdById": "550e8400-e29b-41d4-a716-446655440000",
    "createdByUsername": "admin",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z",
    "status": 1,
    "isDeleted": 0
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

**Error Response** (404 Not Found):
```json
{
  "status": 404,
  "message": "Career not found",
  "error": "Career with id 1 not found",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 3. Update Career
**PUT** `/api/v1/careers/{id}`

Updates an existing career.

**Authorization**: Admin only

**Path Parameters**:
- `id` (integer): Career ID

**Request Body**:
```json
{
  "title": "Senior Software Engineer",
  "description": "Lead development of complex software systems",
  "status": 1
}
```

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Career updated successfully",
  "error": null,
  "data": {
    "id": 1,
    "title": "Senior Software Engineer",
    "description": "Lead development of complex software systems",
    "createdById": "550e8400-e29b-41d4-a716-446655440000",
    "createdByUsername": "admin",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:01:00Z",
    "status": 1,
    "isDeleted": 0
  },
  "timestamp": "2024-01-01T00:01:00Z"
}
```

### 4. Delete Career
**DELETE** `/api/v1/careers/{id}`

Deletes a career permanently.

**Authorization**: Admin only

**Path Parameters**:
- `id` (integer): Career ID

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Career deleted successfully",
  "error": null,
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 5. Change Career Status
**PATCH** `/api/v1/careers/{id}/status`

Changes the status of a career.

**Authorization**: Admin only

**Path Parameters**:
- `id` (integer): Career ID

**Query Parameters**:
- `status` (integer): New status value (0 = inactive, 1 = active)

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Career status updated successfully",
  "error": null,
  "data": {
    "id": 1,
    "title": "Software Engineer",
    "description": "Develop and maintain software applications",
    "createdById": "550e8400-e29b-41d4-a716-446655440000",
    "createdByUsername": "admin",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:01:00Z",
    "status": 0,
    "isDeleted": 0
  },
  "timestamp": "2024-01-01T00:01:00Z"
}
```

### 6. Get All Careers
**GET** `/api/v1/careers`

Retrieves all careers.

**Authorization**: Public

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Careers retrieved successfully",
  "error": null,
  "data": [
    {
      "id": 1,
      "title": "Software Engineer",
      "description": "Develop and maintain software applications",
      "createdById": "550e8400-e29b-41d4-a716-446655440000",
      "createdByUsername": "admin",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "status": 1,
      "isDeleted": 0
    },
    {
      "id": 2,
      "title": "Data Scientist",
      "description": "Analyze data and build machine learning models",
      "createdById": "550e8400-e29b-41d4-a716-446655440000",
      "createdByUsername": "admin",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "status": 1,
      "isDeleted": 0
    }
  ],
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 7. Filter Careers
**POST** `/api/v1/careers/filter`

Filters careers with pagination, sorting, and search criteria.

**Authorization**: Public

**Request Body**:
```json
{
  "filters": [
    {
      "fieldName": "title",
      "operation": "LIKE",
      "value": "engineer",
      "logicType": "AND"
    },
    {
      "fieldName": "status",
      "operation": "EQUALS",
      "value": 1,
      "logicType": "AND"
    }
  ],
  "sorts": [
    {
      "fieldName": "title",
      "direction": "ASC"
    }
  ],
  "page": 0,
  "size": 10
}
```

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Careers filtered successfully",
  "error": null,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Software Engineer",
        "description": "Develop and maintain software applications",
        "createdById": "550e8400-e29b-41d4-a716-446655440000",
        "createdByUsername": "admin",
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T00:00:00Z",
        "status": 1,
        "isDeleted": 0
      }
    ],
    "pageable": {
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "pageNumber": 0,
      "pageSize": 10,
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "empty": false
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 8. Get Careers Count
**GET** `/api/v1/careers/count`

Returns the total number of careers.

**Authorization**: Public

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Careers count retrieved successfully",
  "error": null,
  "data": 5,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Filter Operations

The following filter operations are supported:

- `EQUALS`: Exact match
- `LIKE`: Contains (case-sensitive)
- `ILIKE`: Contains (case-insensitive)
- `GREATER_THAN`: Greater than
- `GREATER_THAN_OR_EQUAL`: Greater than or equal
- `LESS_THAN`: Less than
- `LESS_THAN_OR_EQUAL`: Less than or equal
- `IN`: Value in list
- `NOT_IN`: Value not in list

## Sort Directions

- `ASC`: Ascending order
- `DESC`: Descending order

## Validation Rules

### CareerRequestDto
- `title`: Maximum 255 characters
- `description`: Optional
- `status`: Optional, defaults to 1

## Error Handling

All endpoints return consistent error responses with:
- `status`: HTTP status code
- `message`: Human-readable message
- `error`: Detailed error information
- `data`: Response data (null for errors)
- `timestamp`: Response timestamp

## Example Usage

### Create a new career:
```bash
curl -X POST http://localhost:8080/api/v1/careers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{
    "title": "Frontend Developer",
    "description": "Build user interfaces and web applications",
    "status": 1
  }'
```

### Get all careers:
```bash
curl -X GET http://localhost:8080/api/v1/careers
```

### Filter careers by title:
```bash
curl -X POST http://localhost:8080/api/v1/careers/filter \
  -H "Content-Type: application/json" \
  -d '{
    "filters": [
      {
        "fieldName": "title",
        "operation": "LIKE",
        "value": "developer"
      }
    ],
    "page": 0,
    "size": 10
  }'
```

### Update a career:
```bash
curl -X PUT http://localhost:8080/api/v1/careers/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{
    "title": "Senior Frontend Developer",
    "description": "Lead frontend development and mentor junior developers",
    "status": 1
  }'
```

### Change career status:
```bash
curl -X PATCH "http://localhost:8080/api/v1/careers/1/status?status=0" \
  -H "Authorization: Bearer <admin-token>"
```

### Delete a career:
```bash
curl -X DELETE http://localhost:8080/api/v1/careers/1 \
  -H "Authorization: Bearer <admin-token>"
```
