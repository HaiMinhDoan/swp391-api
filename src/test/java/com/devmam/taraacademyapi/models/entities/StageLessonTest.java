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

@DisplayName("StageLesson Entity Tests")
class StageLessonTest {

    private Validator validator;
    private StageLesson stageLesson;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        stageLesson = new StageLesson();
    }

    @Test
    @DisplayName("Test StageLesson NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(stageLesson);
        assertNull(stageLesson.getId());
        assertNull(stageLesson.getTitle());
    }

    @Test
    @DisplayName("Test StageLesson AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String title = "Stage 1: Introduction";
        String description = "Introduction stage";
        Integer orderIndex = 1;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        StageLesson stageLesson = new StageLesson(id, null, title, description, orderIndex, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, stageLesson.getId());
        assertEquals(title, stageLesson.getTitle());
        assertEquals(description, stageLesson.getDescription());
        assertEquals(orderIndex, stageLesson.getOrderIndex());
    }

    @Test
    @DisplayName("Test StageLesson Builder")
    void testBuilder() {
        String title = "Stage 2: Advanced";
        Integer orderIndex = 2;

        StageLesson stageLesson = StageLesson.builder()
                .title(title)
                .orderIndex(orderIndex)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(title, stageLesson.getTitle());
        assertEquals(orderIndex, stageLesson.getOrderIndex());
    }

    @Test
    @DisplayName("Test StageLesson Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String title = "Test Stage";
        String description = "Test Description";
        Integer orderIndex = 1;

        stageLesson.setId(id);
        stageLesson.setTitle(title);
        stageLesson.setDescription(description);
        stageLesson.setOrderIndex(orderIndex);

        assertEquals(id, stageLesson.getId());
        assertEquals(title, stageLesson.getTitle());
        assertEquals(description, stageLesson.getDescription());
        assertEquals(orderIndex, stageLesson.getOrderIndex());
    }

    @Test
    @DisplayName("Test Title validation - should accept valid title")
    void testTitleValidation_Valid() {
        stageLesson.setTitle("Valid Title");
        Set<ConstraintViolation<StageLesson>> violations = validator.validate(stageLesson);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Title validation - should reject title exceeding max length")
    void testTitleValidation_ExceedsMaxLength() {
        String longTitle = "a".repeat(256); // 256 characters, max is 255
        stageLesson.setTitle(longTitle);
        Set<ConstraintViolation<StageLesson>> violations = validator.validate(stageLesson);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Description - should handle long descriptions")
    void testDescription() {
        String longDescription = "a".repeat(1000);
        stageLesson.setDescription(longDescription);
        assertEquals(longDescription, stageLesson.getDescription());
    }

    @Test
    @DisplayName("Test OrderIndex - should handle order index values")
    void testOrderIndex() {
        stageLesson.setOrderIndex(1);
        assertEquals(1, stageLesson.getOrderIndex());

        stageLesson.setOrderIndex(10);
        assertEquals(10, stageLesson.getOrderIndex());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        stageLesson.setStatus(1);
        stageLesson.setIsDeleted(0);
        assertEquals(1, stageLesson.getStatus());
        assertEquals(0, stageLesson.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        stageLesson.setCreatedAt(now);
        stageLesson.setUpdatedAt(later);

        assertEquals(now, stageLesson.getCreatedAt());
        assertEquals(later, stageLesson.getUpdatedAt());
    }
}

