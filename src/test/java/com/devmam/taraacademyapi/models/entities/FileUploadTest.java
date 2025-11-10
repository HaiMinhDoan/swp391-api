package com.devmam.taraacademyapi.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileUpload Entity Tests")
class FileUploadTest {

    private Validator validator;
    private FileUpload fileUpload;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        fileUpload = new FileUpload();
    }

    @Test
    @DisplayName("Test FileUpload NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(fileUpload);
        assertNull(fileUpload.getId());
        assertNull(fileUpload.getFileName());
    }

    @Test
    @DisplayName("Test FileUpload AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String fileName = "test.pdf";
        String filePath = "/uploads/test.pdf";
        Long fileSize = 1024L;
        String fileType = "application/pdf";
        String fileRef = "DOCUMENT";
        Integer referenceId = 100;
        String description = "Test file";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        FileUpload fileUpload = new FileUpload(id, fileName, filePath, fileSize, fileType, fileRef, referenceId, description, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, fileUpload.getId());
        assertEquals(fileName, fileUpload.getFileName());
        assertEquals(filePath, fileUpload.getFilePath());
        assertEquals(fileSize, fileUpload.getFileSize());
        assertEquals(fileType, fileUpload.getFileType());
        assertEquals(fileRef, fileUpload.getFileRef());
        assertEquals(referenceId, fileUpload.getReferenceId());
        assertEquals(description, fileUpload.getDescription());
    }

    @Test
    @DisplayName("Test FileUpload Builder")
    void testBuilder() {
        String fileName = "document.pdf";
        String filePath = "/uploads/document.pdf";
        Long fileSize = 2048L;

        FileUpload fileUpload = FileUpload.builder()
                .fileName(fileName)
                .filePath(filePath)
                .fileSize(fileSize)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(fileName, fileUpload.getFileName());
        assertEquals(filePath, fileUpload.getFilePath());
        assertEquals(fileSize, fileUpload.getFileSize());
    }

    @Test
    @DisplayName("Test FileUpload Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String fileName = "test.jpg";
        String filePath = "/uploads/test.jpg";
        Long fileSize = 512L;
        String fileType = "image/jpeg";
        String fileRef = "IMAGE";
        Integer referenceId = 200;

        fileUpload.setId(id);
        fileUpload.setFileName(fileName);
        fileUpload.setFilePath(filePath);
        fileUpload.setFileSize(fileSize);
        fileUpload.setFileType(fileType);
        fileUpload.setFileRef(fileRef);
        fileUpload.setReferenceId(referenceId);

        assertEquals(id, fileUpload.getId());
        assertEquals(fileName, fileUpload.getFileName());
        assertEquals(filePath, fileUpload.getFilePath());
        assertEquals(fileSize, fileUpload.getFileSize());
        assertEquals(fileType, fileUpload.getFileType());
        assertEquals(fileRef, fileUpload.getFileRef());
        assertEquals(referenceId, fileUpload.getReferenceId());
    }

    @Test
    @DisplayName("Test FileName validation - should reject null fileName")
    void testFileNameValidation_Null() {
        fileUpload.setFileName(null);
        fileUpload.setFilePath("/path/to/file");
        Set<ConstraintViolation<FileUpload>> violations = validator.validate(fileUpload);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fileName")));
    }

    @Test
    @DisplayName("Test FileName validation - should accept valid fileName")
    void testFileNameValidation_Valid() {
        fileUpload.setFileName("valid.pdf");
        fileUpload.setFilePath("/path/to/file");
        Set<ConstraintViolation<FileUpload>> violations = validator.validate(fileUpload);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test FileName validation - should reject fileName exceeding max length")
    void testFileNameValidation_ExceedsMaxLength() {
        String longFileName = "a".repeat(256); // 256 characters, max is 255
        fileUpload.setFileName(longFileName);
        fileUpload.setFilePath("/path/to/file");
        Set<ConstraintViolation<FileUpload>> violations = validator.validate(fileUpload);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test FilePath validation - should reject null filePath")
    void testFilePathValidation_Null() {
        fileUpload.setFileName("test.pdf");
        fileUpload.setFilePath(null);
        Set<ConstraintViolation<FileUpload>> violations = validator.validate(fileUpload);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("filePath")));
    }

    @Test
    @DisplayName("Test FileType validation - should accept valid file type")
    void testFileTypeValidation_Valid() {
        fileUpload.setFileName("test.pdf");
        fileUpload.setFilePath("/path/to/file");
        fileUpload.setFileType("application/pdf");
        Set<ConstraintViolation<FileUpload>> violations = validator.validate(fileUpload);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test FileSize - should handle file size values")
    void testFileSize() {
        fileUpload.setFileSize(0L);
        assertEquals(0L, fileUpload.getFileSize());

        fileUpload.setFileSize(1024L);
        assertEquals(1024L, fileUpload.getFileSize());

        fileUpload.setFileSize(1048576L);
        assertEquals(1048576L, fileUpload.getFileSize());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        fileUpload.setStatus(1);
        fileUpload.setIsDeleted(0);
        assertEquals(1, fileUpload.getStatus());
        assertEquals(0, fileUpload.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        fileUpload.setCreatedAt(now);
        fileUpload.setUpdatedAt(later);

        assertEquals(now, fileUpload.getCreatedAt());
        assertEquals(later, fileUpload.getUpdatedAt());
    }
}

