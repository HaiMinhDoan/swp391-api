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

@DisplayName("Lesson Entity Tests")
class LessonTest {

    private Validator validator;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        lesson = new Lesson();
    }

    @Test
    @DisplayName("Test Lesson NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(lesson);
        assertNull(lesson.getId());
        assertNull(lesson.getTitle());
    }

    @Test
    @DisplayName("Test Lesson AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String title = "Introduction to Java";
        String content = "Lesson content";
        String videoUrl = "https://example.com/video.mp4";
        Integer orderIndex = 1;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Lesson lesson = new Lesson(id, null, title, content, videoUrl, orderIndex, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, lesson.getId());
        assertEquals(title, lesson.getTitle());
        assertEquals(content, lesson.getContent());
        assertEquals(videoUrl, lesson.getVideoUrl());
        assertEquals(orderIndex, lesson.getOrderIndex());
    }

    @Test
    @DisplayName("Test Lesson Builder")
    void testBuilder() {
        String title = "Advanced Java";
        String content = "Advanced content";
        Integer orderIndex = 2;

        Lesson lesson = Lesson.builder()
                .title(title)
                .content(content)
                .orderIndex(orderIndex)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(title, lesson.getTitle());
        assertEquals(content, lesson.getContent());
        assertEquals(orderIndex, lesson.getOrderIndex());
    }

    @Test
    @DisplayName("Test Lesson Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String title = "Lesson Title";
        String content = "Lesson Content";
        String videoUrl = "https://example.com/video.mp4";
        Integer orderIndex = 1;

        lesson.setId(id);
        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setVideoUrl(videoUrl);
        lesson.setOrderIndex(orderIndex);

        assertEquals(id, lesson.getId());
        assertEquals(title, lesson.getTitle());
        assertEquals(content, lesson.getContent());
        assertEquals(videoUrl, lesson.getVideoUrl());
        assertEquals(orderIndex, lesson.getOrderIndex());
    }

    @Test
    @DisplayName("Test Title validation - should accept valid title")
    void testTitleValidation_Valid() {
        lesson.setTitle("Valid Title");
        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Title validation - should reject title exceeding max length")
    void testTitleValidation_ExceedsMaxLength() {
        String longTitle = "a".repeat(256); // 256 characters, max is 255
        lesson.setTitle(longTitle);
        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Content - should handle long content")
    void testContent() {
        String longContent = "a".repeat(1000);
        lesson.setContent(longContent);
        assertEquals(longContent, lesson.getContent());
    }

    @Test
    @DisplayName("Test VideoUrl - should handle video URLs")
    void testVideoUrl() {
        String videoUrl = "https://example.com/video.mp4";
        lesson.setVideoUrl(videoUrl);
        assertEquals(videoUrl, lesson.getVideoUrl());
    }

    @Test
    @DisplayName("Test OrderIndex - should handle order index values")
    void testOrderIndex() {
        lesson.setOrderIndex(1);
        assertEquals(1, lesson.getOrderIndex());

        lesson.setOrderIndex(10);
        assertEquals(10, lesson.getOrderIndex());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        lesson.setStatus(1);
        lesson.setIsDeleted(0);
        assertEquals(1, lesson.getStatus());
        assertEquals(0, lesson.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        lesson.setCreatedAt(now);
        lesson.setUpdatedAt(later);

        assertEquals(now, lesson.getCreatedAt());
        assertEquals(later, lesson.getUpdatedAt());
    }
}

