package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BulkFileUploadRequestDto;
import com.devmam.taraacademyapi.models.dto.request.FileUploadMultipartRequestDto;
import com.devmam.taraacademyapi.models.dto.request.FileUploadRequestDto;
import com.devmam.taraacademyapi.models.dto.response.*;
import com.devmam.taraacademyapi.models.entities.FileUpload;
import com.devmam.taraacademyapi.service.FileOperationsService;
import com.devmam.taraacademyapi.service.impl.entities.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/files")
@PreAuthorize("permitAll()")
public class FileUploadController extends BaseController<FileUpload, Integer, FileUploadRequestDto, FileUploadResponseDto> {

    @Autowired
    private FileOperationsService fileOperationsService;

    public FileUploadController(FileUploadService fileUploadService) {
        super(fileUploadService);
    }

    @Override
    protected FileUploadResponseDto toResponseDto(FileUpload fileUpload) {
        FileUploadResponseDto dto = FileUploadResponseDto.toDTO(fileUpload);
        dto.setPublicUrl(fileOperationsService.getPublicUrl(fileUpload.getFilePath()));
        return dto;
    }

    @Override
    protected FileUpload toEntity(FileUploadRequestDto requestDto) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(requestDto.getFileName());
        fileUpload.setFilePath(requestDto.getFilePath());
        fileUpload.setFileType(requestDto.getFileType());
        fileUpload.setFileSize(requestDto.getFileSize());
        fileUpload.setReferenceId(requestDto.getReferenceId());
        fileUpload.setDescription(requestDto.getDescription());
        fileUpload.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        fileUpload.setIsDeleted(0);
        fileUpload.setCreatedAt(Instant.now());
        fileUpload.setUpdatedAt(Instant.now());
        return fileUpload;
    }

    @Override
    protected Page<FileUploadResponseDto> convertPage(Page<FileUpload> fileUploadPage) {
        return FileUploadResponseDto.convertPage(fileUploadPage);
    }

    @Override
    protected String getEntityName() {
        return "File";
    }

    /**
     * Upload single file with multipart form data
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseData<FileUploadResultDto>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "fileRef", required = false) String fileRef,
            @RequestParam(value = "referenceId", required = false) Integer referenceId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "customFileName", required = false) String customFileName) {

        try {
            FileUploadMultipartRequestDto request = FileUploadMultipartRequestDto.builder()
                    .file(file)
                    .fileType(fileType)
                    .fileRef(fileRef)
                    .referenceId(referenceId)
                    .description(description)
                    .status(status)
                    .customFileName(customFileName)
                    .build();

            FileUploadResultDto result = fileOperationsService.uploadFile(request);

            HttpStatus httpStatus = result.getSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(httpStatus)
                    .body(ResponseData.<FileUploadResultDto>builder()
                            .status(httpStatus.value())
                            .message(result.getSuccess() ? "File uploaded successfully" : "File upload failed")
                            .error(result.getErrorMessage())
                            .data(result)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<FileUploadResultDto>builder()
                            .status(500)
                            .message("File upload failed")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Upload multiple files
     */
    @PostMapping(value = "/upload/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseData<BulkFileUploadResultDto>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "fileRef", required = false) String fileRef,
            @RequestParam(value = "referenceId", required = false) Integer referenceId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false) Integer status) {

        try {
            BulkFileUploadRequestDto request = BulkFileUploadRequestDto.builder()
                    .files(files)
                    .fileType(fileType)
                    .fileRef(fileRef)
                    .referenceId(referenceId)
                    .description(description)
                    .status(status)
                    .build();

            BulkFileUploadResultDto result = fileOperationsService.uploadMultipleFiles(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<BulkFileUploadResultDto>builder()
                            .status(201)
                            .message("Bulk file upload completed")
                            .error(null)
                            .data(result)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<BulkFileUploadResultDto>builder()
                            .status(500)
                            .message("Bulk file upload failed")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Download file by ID
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer id) {
        try {
            byte[] fileData = fileOperationsService.downloadFile(id);
            Optional<FileUpload> fileMetadata = fileOperationsService.getFileMetadata(id);

            if (fileMetadata.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            FileUpload file = fileMetadata.get();
            String contentType = file.getFileType() != null ? file.getFileType() : "application/octet-stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", file.getFileName());
            headers.setContentLength(fileData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get file metadata by ID
     */
    @GetMapping("/{id}/metadata")
    public ResponseEntity<ResponseData<FileUploadResponseDto>> getFileMetadata(@PathVariable Integer id) {
        try {
            Optional<FileUpload> fileMetadata = fileOperationsService.getFileMetadata(id);

            if (fileMetadata.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<FileUploadResponseDto>builder()
                                .status(404)
                                .message("File not found")
                                .error("File with id " + id + " not found")
                                .data(null)
                                .build());
            }

            FileUploadResponseDto responseDto = toResponseDto(fileMetadata.get());

            return ResponseEntity.ok(ResponseData.<FileUploadResponseDto>builder()
                    .status(200)
                    .message("File metadata retrieved successfully")
                    .error(null)
                    .data(responseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<FileUploadResponseDto>builder()
                            .status(500)
                            .message("Failed to get file metadata")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Get files by reference ID and type
     */
    @GetMapping("/reference/{fileRef}/{referenceId}")
    public ResponseEntity<ResponseData<List<FileUploadResponseDto>>> getFilesByReference(
            @PathVariable String fileRef,
            @PathVariable Integer referenceId) {
        try {
            List<FileUpload> files = fileOperationsService.getFilesByReference(fileRef, referenceId);
            List<FileUploadResponseDto> responseDtos = files.stream()
                    .map(this::toResponseDto)
                    .toList();

            return ResponseEntity.ok(ResponseData.<List<FileUploadResponseDto>>builder()
                    .status(200)
                    .message("Files retrieved successfully")
                    .error(null)
                    .data(responseDtos)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<FileUploadResponseDto>>builder()
                            .status(500)
                            .message("Failed to get files by reference")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Get files by type
     */
    @GetMapping("/type/{fileType}")
    public ResponseEntity<ResponseData<List<FileUploadResponseDto>>> getFilesByType(@PathVariable String fileType) {
        try {
            List<FileUpload> files = fileOperationsService.getFilesByType(fileType);
            List<FileUploadResponseDto> responseDtos = files.stream()
                    .map(this::toResponseDto)
                    .toList();

            return ResponseEntity.ok(ResponseData.<List<FileUploadResponseDto>>builder()
                    .status(200)
                    .message("Files retrieved successfully")
                    .error(null)
                    .data(responseDtos)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<FileUploadResponseDto>>builder()
                            .status(500)
                            .message("Failed to get files by type")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Generate presigned upload URL
     */
    @PostMapping("/presigned-upload-url")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseData<String>> generatePresignedUploadUrl(
            @RequestParam String fileName,
            @RequestParam String contentType,
            @RequestParam(defaultValue = "3600") int expirySeconds) {
        try {
            String presignedUrl = fileOperationsService.generatePresignedUploadUrl(fileName, contentType, expirySeconds);

            return ResponseEntity.ok(ResponseData.<String>builder()
                    .status(200)
                    .message("Presigned upload URL generated successfully")
                    .error(null)
                    .data(presignedUrl)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<String>builder()
                            .status(500)
                            .message("Failed to generate presigned upload URL")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Generate presigned download URL
     */
    @PostMapping("/presigned-download-url")
    public ResponseEntity<ResponseData<String>> generatePresignedDownloadUrl(
            @RequestParam String objectName,
            @RequestParam(defaultValue = "3600") int expirySeconds) {
        try {
            String presignedUrl = fileOperationsService.generatePresignedDownloadUrl(objectName, expirySeconds);

            return ResponseEntity.ok(ResponseData.<String>builder()
                    .status(200)
                    .message("Presigned download URL generated successfully")
                    .error(null)
                    .data(presignedUrl)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<String>builder()
                            .status(500)
                            .message("Failed to generate presigned download URL")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Update file metadata
     */
    @PatchMapping("/{id}/metadata")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseData<FileUploadResponseDto>> updateFileMetadata(
            @PathVariable Integer id,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer status) {
        try {
            FileUpload updatedFile = fileOperationsService.updateFileMetadata(id, description, status);
            FileUploadResponseDto responseDto = toResponseDto(updatedFile);

            return ResponseEntity.ok(ResponseData.<FileUploadResponseDto>builder()
                    .status(200)
                    .message("File metadata updated successfully")
                    .error(null)
                    .data(responseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<FileUploadResponseDto>builder()
                            .status(500)
                            .message("Failed to update file metadata")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Get file statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<FileStatisticsDto>> getFileStatistics() {
        try {
            FileStatisticsDto statistics = fileOperationsService.getFileStatistics();

            return ResponseEntity.ok(ResponseData.<FileStatisticsDto>builder()
                    .status(200)
                    .message("File statistics retrieved successfully")
                    .error(null)
                    .data(statistics)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<FileStatisticsDto>builder()
                            .status(500)
                            .message("Failed to get file statistics")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Check if file exists
     */
    @GetMapping("/exists")
    public ResponseEntity<ResponseData<Boolean>> checkFileExists(@RequestParam String objectName) {
        try {
            boolean exists = fileOperationsService.fileExists(objectName);

            return ResponseEntity.ok(ResponseData.<Boolean>builder()
                    .status(200)
                    .message("File existence checked")
                    .error(null)
                    .data(exists)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Boolean>builder()
                            .status(500)
                            .message("Failed to check file existence")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * List files with prefix
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<List<String>>> listFiles(@RequestParam(required = false) String prefix) {
        try {
            List<String> files = fileOperationsService.listFiles(prefix);

            return ResponseEntity.ok(ResponseData.<List<String>>builder()
                    .status(200)
                    .message("Files listed successfully")
                    .error(null)
                    .data(files)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<String>>builder()
                            .status(500)
                            .message("Failed to list files")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Override delete to use file operations service
     */
    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> delete(@PathVariable Integer id) {
        try {
            boolean deleted = fileOperationsService.deleteFile(id);

            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<Void>builder()
                                .status(404)
                                .message("File not found")
                                .error("File with id " + id + " not found")
                                .data(null)
                                .build());
            }

            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(200)
                    .message("File deleted successfully")
                    .error(null)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Void>builder()
                            .status(500)
                            .message("Failed to delete file")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
