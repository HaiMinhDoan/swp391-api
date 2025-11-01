package com.devmam.taraacademyapi.service.impl.utils;

import com.devmam.taraacademyapi.models.dto.request.BulkFileUploadRequestDto;
import com.devmam.taraacademyapi.models.dto.request.FileUploadMultipartRequestDto;
import com.devmam.taraacademyapi.models.dto.response.BulkFileUploadResultDto;
import com.devmam.taraacademyapi.models.dto.response.FileStatisticsDto;
import com.devmam.taraacademyapi.models.dto.response.FileUploadResultDto;
import com.devmam.taraacademyapi.models.entities.FileUpload;
import com.devmam.taraacademyapi.repository.FileUploadRepository;
import com.devmam.taraacademyapi.service.FileOperationsService;
import com.devmam.taraacademyapi.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileOperationsServiceImpl implements FileOperationsService {

    private static final Logger logger = LoggerFactory.getLogger(FileOperationsServiceImpl.class);

    @Autowired
    private MinioService minioService;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    // Maximum file size: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    // Allowed file types
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain", "text/csv", "application/zip", "video/mp4", "audio/mpeg"
    );

    @Override
    public FileUploadResultDto uploadFile(FileUploadMultipartRequestDto request) {
        try {
            MultipartFile file = request.getFile();

            // Validate file
            String validationError = validateFile(file);
            if (validationError != null) {
                return FileUploadResultDto.builder()
                        .success(false)
                        .errorMessage(validationError)
                        .build();
            }

            // Generate unique object name
            String objectName = generateObjectName(file, request.getCustomFileName());

            // Upload to MinIO
            String uploadedObjectName = minioService.upload(file, objectName);

            // Save metadata to database
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(file.getOriginalFilename());
            fileUpload.setFilePath(uploadedObjectName);
            fileUpload.setFileType(request.getFileType() != null ? request.getFileType() : file.getContentType());
            fileUpload.setFileRef(request.getFileRef());
            fileUpload.setFileSize(file.getSize());
            fileUpload.setReferenceId(request.getReferenceId());
            fileUpload.setDescription(request.getDescription());
            fileUpload.setStatus(request.getStatus() != null ? request.getStatus() : 1);
            fileUpload.setIsDeleted(0);
            fileUpload.setCreatedAt(Instant.now());
            fileUpload.setUpdatedAt(Instant.now());

            FileUpload savedFile = fileUploadRepository.save(fileUpload);

            return FileUploadResultDto.builder()
                    .id(savedFile.getId())
                    .fileName(savedFile.getFileName())
                    .filePath(savedFile.getFilePath())
                    .fileType(savedFile.getFileType())
                    .fileSize(savedFile.getFileSize())
                    .publicUrl(minioService.getPublicUrl(uploadedObjectName))
                    .downloadUrl(minioService.generatePresignedDownloadUrl(uploadedObjectName, 3600))
                    .success(true)
                    .uploadedAt(savedFile.getCreatedAt())
                    .build();

        } catch (Exception e) {
            logger.error("Error uploading file: {}", e.getMessage(), e);
            return FileUploadResultDto.builder()
                    .success(false)
                    .errorMessage("Upload failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public BulkFileUploadResultDto uploadMultipleFiles(BulkFileUploadRequestDto request) {
        List<FileUploadResultDto> results = new ArrayList<>();
        int successfulUploads = 0;
        int failedUploads = 0;
        long totalSize = 0;

        for (MultipartFile file : request.getFiles()) {
            FileUploadMultipartRequestDto singleRequest = FileUploadMultipartRequestDto.builder()
                    .file(file)
                    .fileType(request.getFileType())
                    .fileRef(request.getFileRef())
                    .referenceId(request.getReferenceId())
                    .description(request.getDescription())
                    .status(request.getStatus())
                    .build();

            FileUploadResultDto result = uploadFile(singleRequest);
            results.add(result);

            if (result.getSuccess()) {
                successfulUploads++;
                totalSize += result.getFileSize() != null ? result.getFileSize() : 0;
            } else {
                failedUploads++;
            }
        }

        String overallStatus = failedUploads == 0 ? "SUCCESS" :
                successfulUploads == 0 ? "FAILED" : "PARTIAL";

        return BulkFileUploadResultDto.builder()
                .results(results)
                .totalFiles(request.getFiles().size())
                .successfulUploads(successfulUploads)
                .failedUploads(failedUploads)
                .totalSize(totalSize)
                .overallStatus(overallStatus)
                .build();
    }

    @Override
    public byte[] downloadFile(Integer fileId) {
        Optional<FileUpload> fileUpload = fileUploadRepository.findById(fileId);
        if (fileUpload.isEmpty()) {
            throw new RuntimeException("File not found with ID: " + fileId);
        }

        try {
            return minioService.download(fileUpload.get().getFilePath());
        } catch (Exception e) {
            logger.error("Error downloading file with ID {}: {}", fileId, e.getMessage());
            throw new RuntimeException("Failed to download file: " + e.getMessage());
        }
    }

    @Override
    public byte[] downloadFileByObjectName(String objectName) {
        try {
            return minioService.download(objectName);
        } catch (Exception e) {
            logger.error("Error downloading file with object name {}: {}", objectName, e.getMessage());
            throw new RuntimeException("Failed to download file: " + e.getMessage());
        }
    }

    @Override
    public Optional<FileUpload> getFileMetadata(Integer fileId) {
        return fileUploadRepository.findById(fileId);
    }

    @Override
    public List<FileUpload> getFilesByReference(String fileRef, Integer referenceId) {
        return fileUploadRepository.findActiveFilesByRefAndReference(fileRef, referenceId);
    }

    @Override
    public boolean deleteFile(Integer fileId) {
        Optional<FileUpload> fileUpload = fileUploadRepository.findById(fileId);
        if (fileUpload.isEmpty()) {
            return false;
        }

        try {
            // Delete from MinIO
            boolean minioDeleted = minioService.delete(fileUpload.get().getFilePath());

            // Soft delete from database
            fileUpload.get().setIsDeleted(1);
            fileUpload.get().setUpdatedAt(Instant.now());
            fileUploadRepository.save(fileUpload.get());

            return minioDeleted;
        } catch (Exception e) {
            logger.error("Error deleting file with ID {}: {}", fileId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteFileByObjectName(String objectName) {
        try {
            return minioService.delete(objectName);
        } catch (Exception e) {
            logger.error("Error deleting file with object name {}: {}", objectName, e.getMessage());
            return false;
        }
    }

    @Override
    public String generatePresignedUploadUrl(String fileName, String contentType, int expirySeconds) {
        try {
            String objectName = generateObjectName(fileName);
            return minioService.generatePresignedUploadUrl(objectName, expirySeconds);
        } catch (Exception e) {
            logger.error("Error generating presigned upload URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate presigned upload URL: " + e.getMessage());
        }
    }

    @Override
    public String generatePresignedDownloadUrl(String objectName, int expirySeconds) {
        try {
            return minioService.generatePresignedDownloadUrl(objectName, expirySeconds);
        } catch (Exception e) {
            logger.error("Error generating presigned download URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate presigned download URL: " + e.getMessage());
        }
    }

    @Override
    public String getPublicUrl(String objectName) {
        return minioService.getPublicUrl(objectName);
    }

    @Override
    public boolean fileExists(String objectName) {
        return minioService.exists(objectName);
    }

    @Override
    public Long getFileSize(String objectName) {
        try {
            return minioService.getMinioClient().statObject(
                    io.minio.StatObjectArgs.builder()
                            .bucket(minioService.getBucketName())
                            .object(objectName)
                            .build()
            ).size();
        } catch (Exception e) {
            logger.error("Error getting file size for {}: {}", objectName, e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> listFiles(String prefix) {
        try {
            return minioService.list(prefix);
        } catch (Exception e) {
            logger.error("Error listing files with prefix {}: {}", prefix, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public FileUpload updateFileMetadata(Integer fileId, String description, Integer status) {
        Optional<FileUpload> fileUpload = fileUploadRepository.findById(fileId);
        if (fileUpload.isEmpty()) {
            throw new RuntimeException("File not found with ID: " + fileId);
        }

        FileUpload file = fileUpload.get();
        if (description != null) {
            file.setDescription(description);
        }
        if (status != null) {
            file.setStatus(status);
        }
        file.setUpdatedAt(Instant.now());

        return fileUploadRepository.save(file);
    }

    @Override
    public List<FileUpload> getFilesByType(String fileType) {
        return fileUploadRepository.findByFileType(fileType).stream()
                .filter(file -> file.getIsDeleted() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public FileStatisticsDto getFileStatistics() {
        List<FileUpload> allFiles = fileUploadRepository.findAll().stream()
                .filter(file -> file.getIsDeleted() == 0)
                .collect(Collectors.toList());

        long totalFiles = allFiles.size();
        long totalSize = allFiles.stream()
                .mapToLong(file -> file.getFileSize() != null ? file.getFileSize() : 0)
                .sum();
        long averageFileSize = totalFiles > 0 ? totalSize / totalFiles : 0;

        Map<String, Long> filesByType = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getFileType() != null ? file.getFileType() : "unknown",
                        Collectors.counting()
                ));

        Map<String, Long> filesByStatus = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getStatus() != null ? file.getStatus().toString() : "unknown",
                        Collectors.counting()
                ));

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusWeeks(1);
        LocalDate monthAgo = today.minusMonths(1);

        long filesUploadedToday = allFiles.stream()
                .filter(file -> file.getCreatedAt() != null &&
                        file.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().equals(today))
                .count();

        long filesUploadedThisWeek = allFiles.stream()
                .filter(file -> file.getCreatedAt() != null &&
                        file.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(weekAgo))
                .count();

        long filesUploadedThisMonth = allFiles.stream()
                .filter(file -> file.getCreatedAt() != null &&
                        file.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(monthAgo))
                .count();

        return FileStatisticsDto.builder()
                .totalFiles(totalFiles)
                .totalSize(totalSize)
                .averageFileSize(averageFileSize)
                .filesByType(filesByType)
                .filesByStatus(filesByStatus)
                .filesUploadedToday(filesUploadedToday)
                .filesUploadedThisWeek(filesUploadedThisWeek)
                .filesUploadedThisMonth(filesUploadedThisMonth)
                .build();
    }

    private String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return "File size exceeds maximum allowed size of 50MB";
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_FILE_TYPES.contains(contentType)) {
            return "File type not allowed: " + contentType;
        }

        return null;
    }

    private String generateObjectName(MultipartFile file, String customFileName) {
        String fileName = customFileName != null ? customFileName : file.getOriginalFilename();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = "";

        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        return fileName + "_" + timestamp + extension;
    }

    private String generateObjectName(String fileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = "";

        if (fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        return fileName + "_" + timestamp + extension;
    }
}
