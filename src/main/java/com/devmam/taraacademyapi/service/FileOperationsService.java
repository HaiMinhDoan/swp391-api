package com.devmam.taraacademyapi.service;

import com.devmam.taraacademyapi.models.dto.request.BulkFileUploadRequestDto;
import com.devmam.taraacademyapi.models.dto.request.FileUploadMultipartRequestDto;
import com.devmam.taraacademyapi.models.dto.response.BulkFileUploadResultDto;
import com.devmam.taraacademyapi.models.dto.response.FileStatisticsDto;
import com.devmam.taraacademyapi.models.dto.response.FileUploadResultDto;
import com.devmam.taraacademyapi.models.entities.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for comprehensive file operations
 */
public interface FileOperationsService {
    
    /**
     * Upload single file with metadata
     */
    FileUploadResultDto uploadFile(FileUploadMultipartRequestDto request);
    
    /**
     * Upload multiple files
     */
    BulkFileUploadResultDto uploadMultipleFiles(BulkFileUploadRequestDto request);
    
    /**
     * Download file by ID
     */
    byte[] downloadFile(Integer fileId);
    
    /**
     * Download file by object name
     */
    byte[] downloadFileByObjectName(String objectName);
    
    /**
     * Get file metadata by ID
     */
    Optional<FileUpload> getFileMetadata(Integer fileId);
    
    /**
     * Get files by reference ID and type
     */
    List<FileUpload> getFilesByReference(String fileType, Integer referenceId);
    
    /**
     * Delete file by ID
     */
    boolean deleteFile(Integer fileId);
    
    /**
     * Delete file by object name
     */
    boolean deleteFileByObjectName(String objectName);
    
    /**
     * Generate presigned upload URL
     */
    String generatePresignedUploadUrl(String fileName, String contentType, int expirySeconds);
    
    /**
     * Generate presigned download URL
     */
    String generatePresignedDownloadUrl(String objectName, int expirySeconds);
    
    /**
     * Get public URL for file
     */
    String getPublicUrl(String objectName);
    
    /**
     * Check if file exists
     */
    boolean fileExists(String objectName);
    
    /**
     * Get file size
     */
    Long getFileSize(String objectName);
    
    /**
     * List files with prefix
     */
    List<String> listFiles(String prefix);
    
    /**
     * Update file metadata
     */
    FileUpload updateFileMetadata(Integer fileId, String description, Integer status);
    
    /**
     * Get files by type
     */
    List<FileUpload> getFilesByType(String fileType);
    
    /**
     * Get file statistics
     */
    FileStatisticsDto getFileStatistics();
}
