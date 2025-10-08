# Slide API Documentation

## Overview
This document describes the CRUD API endpoints for managing Slide entities in the Tara Academy API.

## Base URL
```
/api/v1/slides
```

## Authentication
- **Public endpoints**: GET operations (view slides)
- **Admin endpoints**: POST, PUT, DELETE, PATCH operations (manage slides)
- Admin endpoints require `ADMIN` role authentication

## API Endpoints

### 1. Create Slide
**POST** `/api/v1/slides`

Creates a new slide.

**Authorization**: Admin only

**Request Body**:
```json
{
  "title": "Slide Title",
  "description": "Slide description",
  "imageUrl": "https://example.com/image.jpg",
  "linkUrl": "https://example.com/link",
  "orderIndex": 1,
  "status": 1
}
```

**Response** (201 Created):
```json
{
  "status": 201,
  "message": "Slide created successfully",
  "error": null,
  "data": {
    "id": 1,
    "title": "Slide Title",
    "description": "Slide description",
    "imageUrl": "https://example.com/image.jpg",
    "linkUrl": "https://example.com/link",
    "orderIndex": 1,
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

### 2. Get Slide by ID
**GET** `/api/v1/slides/{id}`

Retrieves a specific slide by its ID.

**Authorization**: Public

**Path Parameters**:
- `id` (integer): Slide ID

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Slide found",
  "error": null,
  "data": {
    "id": 1,
    "title": "Slide Title",
    "description": "Slide description",
    "imageUrl": "https://example.com/image.jpg",
    "linkUrl": "https://example.com/link",
    "orderIndex": 1,
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

**Error Response** (404 Not Found):
```json
{
  "status": 404,
  "message": "Slide not found",
  "error": "Slide with id 1 not found",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 3. Update Slide
**PUT** `/api/v1/slides/{id}`

Updates an existing slide.

**Authorization**: Admin only

**Path Parameters**:
- `id` (integer): Slide ID

**Request Body**:
```json
{
  "title": "Updated Slide Title",
  "description": "Updated description",
  "imageUrl": "https://example.com/new-image.jpg",
  "linkUrl": "https://example.com/new-link",
  "orderIndex": 2,
  "status": 1
}
```

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Slide updated successfully",
  "error": null,
  "data": {
    "id": 1,
    "title": "Updated Slide Title",
    "description": "Updated description",
    "imageUrl": "https://example.com/new-image.jpg",
    "linkUrl": "https://example.com/new-link",
    "orderIndex": 2,
    "createdById": null,
    "createdByUsername": null,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:01:00Z",
    "status": 1,
    "isDeleted": 0
  },
  "timestamp": "2024-01-01T00:01:00Z"
}
```

### 4. Delete Slide
**DELETE** `/api/v1/slides/{id}`

Deletes a slide permanently.

**Authorization**: Admin only

**Path Parameters**:
- `id` (integer): Slide ID

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Slide deleted successfully",
  "error": null,
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 5. Change Slide Status
**PATCH** `/api/v1/slides/{id}/status`

Changes the status of a slide.

**Authorization**: Admin only

**Path Parameters**:
- `id` (integer): Slide ID

**Query Parameters**:
- `status` (integer): New status value (0 = inactive, 1 = active)

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Slide status updated successfully",
  "error": null,
  "data": {
    "id": 1,
    "title": "Slide Title",
    "description": "Slide description",
    "imageUrl": "https://example.com/image.jpg",
    "linkUrl": "https://example.com/link",
    "orderIndex": 1,
    "createdById": null,
    "createdByUsername": null,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:01:00Z",
    "status": 0,
    "isDeleted": 0
  },
  "timestamp": "2024-01-01T00:01:00Z"
}
```

### 6. Get All Slides
**GET** `/api/v1/slides`

Retrieves all slides.

**Authorization**: Public

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Slides retrieved successfully",
  "error": null,
  "data": [
    {
      "id": 1,
      "title": "Slide 1",
      "description": "Description 1",
      "imageUrl": "https://example.com/image1.jpg",
      "linkUrl": "https://example.com/link1",
      "orderIndex": 1,
      "createdById": null,
    "createdByUsername": null,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "status": 1,
      "isDeleted": 0
    },
    {
      "id": 2,
      "title": "Slide 2",
      "description": "Description 2",
      "imageUrl": "https://example.com/image2.jpg",
      "linkUrl": "https://example.com/link2",
      "orderIndex": 2,
      "createdById": null,
    "createdByUsername": null,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "status": 1,
      "isDeleted": 0
    }
  ],
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 7. Filter Slides
**POST** `/api/v1/slides/filter`

Filters slides with pagination, sorting, and search criteria.

**Authorization**: Public

**Request Body**:
```json
{
  "filters": [
    {
      "fieldName": "title",
      "operation": "LIKE",
      "value": "slide",
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
      "fieldName": "orderIndex",
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
  "message": "Slides filtered successfully",
  "error": null,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Slide 1",
        "description": "Description 1",
        "imageUrl": "https://example.com/image1.jpg",
        "linkUrl": "https://example.com/link1",
        "orderIndex": 1,
        "createdById": null,
    "createdByUsername": null,
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

### 8. Get Slides Count
**GET** `/api/v1/slides/count`

Returns the total number of slides.

**Authorization**: Public

**Response** (200 OK):
```json
{
  "status": 200,
  "message": "Slides count retrieved successfully",
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

### SlideRequestDto
- `title`: Maximum 255 characters
- `imageUrl`: Required, cannot be null or blank
- `description`: Optional
- `linkUrl`: Optional
- `orderIndex`: Optional, defaults to 0
- `status`: Optional, defaults to 1

## Error Handling

All endpoints return consistent error responses with:
- `status`: HTTP status code
- `message`: Human-readable message
- `error`: Detailed error information
- `data`: Response data (null for errors)
- `timestamp`: Response timestamp

## Example Usage

### Create a new slide:
```bash
curl -X POST http://localhost:8080/api/v1/slides \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{
    "title": "Welcome Slide",
    "description": "Welcome to our platform",
    "imageUrl": "https://example.com/welcome.jpg",
    "linkUrl": "https://example.com/welcome",
    "orderIndex": 1,
    "status": 1
  }'
```

### Get all slides:
```bash
curl -X GET http://localhost:8080/api/v1/slides
```

### Filter slides by title:
```bash
curl -X POST http://localhost:8080/api/v1/slides/filter \
  -H "Content-Type: application/json" \
  -d '{
    "filters": [
      {
        "fieldName": "title",
        "operation": "LIKE",
        "value": "welcome"
      }
    ],
    "page": 0,
    "size": 10
  }'
```
