# File Operations API Documentation

## Overview
This API provides comprehensive file management operations including upload, download, metadata management, and file statistics. The API uses MinIO for object storage and provides both direct file operations and presigned URL generation for secure file access.

## Base URL
```
/api/v1/files
```

## Authentication
Most endpoints require authentication. Upload and admin operations require `ADMIN` or `USER` roles.

## File Upload Operations

### 1. Upload Single File
**Endpoint:** `POST /api/v1/files/upload`

**Content-Type:** `multipart/form-data`

**Parameters:**
- `file` (required): The file to upload
- `fileType` (optional): Custom file type classification
- `referenceId` (optional): Reference ID for associating files with entities
- `description` (optional): File description
- `status` (optional): File status (default: 1)
- `customFileName` (optional): Custom filename

**Response:**
```json
{
  "status": 201,
  "message": "File uploaded successfully",
  "error": null,
  "data": {
    "id": 1,
    "fileName": "document.pdf",
    "filePath": "document_1234567890.pdf",
    "fileType": "application/pdf",
    "fileSize": 1024000,
    "publicUrl": "http://minio-endpoint/bucket/document_1234567890.pdf",
    "downloadUrl": "http://minio-endpoint/bucket/document_1234567890.pdf?X-Amz-Algorithm=...",
    "success": true,
    "uploadedAt": "2024-01-01T10:00:00Z"
  }
}
```

### 2. Upload Multiple Files
**Endpoint:** `POST /api/v1/files/upload/bulk`

**Content-Type:** `multipart/form-data`

**Parameters:**
- `files` (required): Array of files to upload (max 10 files)
- `fileType` (optional): File type classification for all files
- `referenceId` (optional): Reference ID for all files
- `description` (optional): Description for all files
- `status` (optional): Status for all files

**Response:**
```json
{
  "status": 201,
  "message": "Bulk file upload completed",
  "error": null,
  "data": {
    "results": [
      {
        "id": 1,
        "fileName": "file1.pdf",
        "success": true,
        "fileSize": 1024000
      },
      {
        "fileName": "file2.jpg",
        "success": false,
        "errorMessage": "File type not allowed"
      }
    ],
    "totalFiles": 2,
    "successfulUploads": 1,
    "failedUploads": 1,
    "totalSize": 1024000,
    "overallStatus": "PARTIAL"
  }
}
```

## File Download Operations

### 3. Download File by ID
**Endpoint:** `GET /api/v1/files/{id}/download`

**Response:** Binary file content with appropriate headers

### 4. Generate Presigned Download URL
**Endpoint:** `POST /api/v1/files/presigned-download-url`

**Parameters:**
- `objectName` (required): Object name in storage
- `expirySeconds` (optional): URL expiry time in seconds (default: 3600)

**Response:**
```json
{
  "status": 200,
  "message": "Presigned download URL generated successfully",
  "error": null,
  "data": "http://minio-endpoint/bucket/file.pdf?X-Amz-Algorithm=..."
}
```

## File Metadata Operations

### 5. Get File Metadata
**Endpoint:** `GET /api/v1/files/{id}/metadata`

**Response:**
```json
{
  "status": 200,
  "message": "File metadata retrieved successfully",
  "error": null,
  "data": {
    "id": 1,
    "fileName": "document.pdf",
    "filePath": "document_1234567890.pdf",
    "fileType": "application/pdf",
    "fileSize": 1024000,
    "referenceId": 123,
    "description": "Important document",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z",
    "status": 1,
    "isDeleted": 0,
    "publicUrl": "http://minio-endpoint/bucket/document_1234567890.pdf"
  }
}
```

### 6. Update File Metadata
**Endpoint:** `PATCH /api/v1/files/{id}/metadata`

**Parameters:**
- `description` (optional): New description
- `status` (optional): New status

**Response:**
```json
{
  "status": 200,
  "message": "File metadata updated successfully",
  "error": null,
  "data": {
    "id": 1,
    "fileName": "document.pdf",
    "description": "Updated description",
    "status": 2,
    "updatedAt": "2024-01-01T11:00:00Z"
  }
}
```

## File Query Operations

### 7. Get Files by Reference
**Endpoint:** `GET /api/v1/files/reference/{fileType}/{referenceId}`

**Response:**
```json
{
  "status": 200,
  "message": "Files retrieved successfully",
  "error": null,
  "data": [
    {
      "id": 1,
      "fileName": "document.pdf",
      "fileType": "application/pdf",
      "referenceId": 123,
      "publicUrl": "http://minio-endpoint/bucket/document_1234567890.pdf"
    }
  ]
}
```

### 8. Get Files by Type
**Endpoint:** `GET /api/v1/files/type/{fileType}`

**Response:**
```json
{
  "status": 200,
  "message": "Files retrieved successfully",
  "error": null,
  "data": [
    {
      "id": 1,
      "fileName": "document.pdf",
      "fileType": "application/pdf",
      "fileSize": 1024000,
      "publicUrl": "http://minio-endpoint/bucket/document_1234567890.pdf"
    }
  ]
}
```

## File Management Operations

### 9. Delete File
**Endpoint:** `DELETE /api/v1/files/{id}`

**Response:**
```json
{
  "status": 200,
  "message": "File deleted successfully",
  "error": null,
  "data": null
}
```

### 10. Check File Exists
**Endpoint:** `GET /api/v1/files/exists`

**Parameters:**
- `objectName` (required): Object name to check

**Response:**
```json
{
  "status": 200,
  "message": "File existence checked",
  "error": null,
  "data": true
}
```

### 11. List Files
**Endpoint:** `GET /api/v1/files/list`

**Parameters:**
- `prefix` (optional): Prefix to filter files

**Response:**
```json
{
  "status": 200,
  "message": "Files listed successfully",
  "error": null,
  "data": [
    "document_1234567890.pdf",
    "image_1234567891.jpg"
  ]
}
```

## Presigned URL Operations

### 12. Generate Presigned Upload URL
**Endpoint:** `POST /api/v1/files/presigned-upload-url`

**Parameters:**
- `fileName` (required): Name for the file
- `contentType` (required): MIME type of the file
- `expirySeconds` (optional): URL expiry time in seconds (default: 3600)

**Response:**
```json
{
  "status": 200,
  "message": "Presigned upload URL generated successfully",
  "error": null,
  "data": "http://minio-endpoint/bucket/file.pdf?X-Amz-Algorithm=..."
}
```

## Statistics and Analytics

### 13. Get File Statistics
**Endpoint:** `GET /api/v1/files/statistics`

**Response:**
```json
{
  "status": 200,
  "message": "File statistics retrieved successfully",
  "error": null,
  "data": {
    "totalFiles": 150,
    "totalSize": 52428800,
    "averageFileSize": 349525,
    "filesByType": {
      "application/pdf": 50,
      "image/jpeg": 75,
      "text/plain": 25
    },
    "filesByStatus": {
      "1": 140,
      "2": 10
    },
    "filesUploadedToday": 5,
    "filesUploadedThisWeek": 25,
    "filesUploadedThisMonth": 100
  }
}
```

## Standard CRUD Operations

The API also supports standard CRUD operations inherited from BaseController:

- `GET /api/v1/files` - Get all files
- `GET /api/v1/files/{id}` - Get file by ID
- `POST /api/v1/files` - Create file metadata
- `PUT /api/v1/files/{id}` - Update file metadata
- `PATCH /api/v1/files/{id}/status` - Change file status
- `POST /api/v1/files/filter` - Filter files with pagination
- `GET /api/v1/files/count` - Get file count
- `GET /api/v1/files/{id}/exists` - Check if file exists

## File Validation Rules

### Allowed File Types
- Images: `image/jpeg`, `image/png`, `image/gif`, `image/webp`
- Documents: `application/pdf`, `application/msword`, `application/vnd.openxmlformats-officedocument.wordprocessingml.document`
- Spreadsheets: `application/vnd.ms-excel`, `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- Text: `text/plain`, `text/csv`
- Archives: `application/zip`
- Media: `video/mp4`, `audio/mpeg`

### File Size Limits
- Maximum file size: 50MB per file
- Maximum files per bulk upload: 10 files

## Error Responses

### File Too Large
```json
{
  "status": 400,
  "message": "File upload failed",
  "error": "File size exceeds maximum allowed size of 50MB",
  "data": null
}
```

### File Type Not Allowed
```json
{
  "status": 400,
  "message": "File upload failed",
  "error": "File type not allowed: application/exe",
  "data": null
}
```

### File Not Found
```json
{
  "status": 404,
  "message": "File not found",
  "error": "File with id 123 not found",
  "data": null
}
```

## Security Features

1. **File Type Validation**: Only allowed file types can be uploaded
2. **File Size Limits**: Maximum file size restrictions
3. **Authentication**: Most operations require valid authentication
4. **Authorization**: Admin-only operations are protected
5. **Presigned URLs**: Secure temporary access to files
6. **Soft Delete**: Files are soft-deleted to maintain data integrity

## Usage Examples

### Upload a PDF Document
```bash
curl -X POST "http://localhost:8080/api/v1/files/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@document.pdf" \
  -F "fileType=application/pdf" \
  -F "referenceId=123" \
  -F "description=Important contract"
```

### Download File
```bash
curl -X GET "http://localhost:8080/api/v1/files/1/download" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output downloaded_file.pdf
```

### Generate Presigned Upload URL
```bash
curl -X POST "http://localhost:8080/api/v1/files/presigned-upload-url" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d "fileName=myfile.pdf&contentType=application/pdf&expirySeconds=1800"
```

This comprehensive file operations API provides all necessary functionality for file management in your application, including secure upload/download, metadata management, and analytics.
