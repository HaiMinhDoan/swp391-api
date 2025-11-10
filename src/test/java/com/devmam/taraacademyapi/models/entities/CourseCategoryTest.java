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

@DisplayName("CourseCategory Entity Tests")
class CourseCategoryTest {

    private Validator validator;
    private CourseCategory courseCategory;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        courseCategory = new CourseCategory();
    }

    @Test
    @DisplayName("Test CourseCategory NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(courseCategory);
        assertNull(courseCategory.getId());
        assertNull(courseCategory.getName());
    }

    @Test
    @DisplayName("Test CourseCategory AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String name = "Programming";
        String description = "Programming courses";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        CourseCategory courseCategory = new CourseCategory(id, name, description, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, courseCategory.getId());
        assertEquals(name, courseCategory.getName());
        assertEquals(description, courseCategory.getDescription());
        assertEquals(createdAt, courseCategory.getCreatedAt());
        assertEquals(updatedAt, courseCategory.getUpdatedAt());
        assertEquals(status, courseCategory.getStatus());
        assertEquals(isDeleted, courseCategory.getIsDeleted());
    }

    @Test
    @DisplayName("Test CourseCategory Builder")
    void testBuilder() {
        String name = "Web Development";
        String description = "Web dev courses";

        CourseCategory courseCategory = CourseCategory.builder()
                .name(name)
                .description(description)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(name, courseCategory.getName());
        assertEquals(description, courseCategory.getDescription());
        assertEquals(1, courseCategory.getStatus());
        assertEquals(0, courseCategory.getIsDeleted());
    }

    @Test
    @DisplayName("Test CourseCategory Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String name = "Data Science";
        String description = "Data science courses";

        courseCategory.setId(id);
        courseCategory.setName(name);
        courseCategory.setDescription(description);
        courseCategory.setStatus(1);
        courseCategory.setIsDeleted(0);

        assertEquals(id, courseCategory.getId());
        assertEquals(name, courseCategory.getName());
        assertEquals(description, courseCategory.getDescription());
        assertEquals(1, courseCategory.getStatus());
        assertEquals(0, courseCategory.getIsDeleted());
    }

    @Test
    @DisplayName("Test Name validation - should accept valid name")
    void testNameValidation_Valid() {
        courseCategory.setName("Valid Name");
        Set<ConstraintViolation<CourseCategory>> violations = validator.validate(courseCategory);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Name validation - should reject name exceeding max length")
    void testNameValidation_ExceedsMaxLength() {
        String longName = "a".repeat(256); // 256 characters, max is 255
        courseCategory.setName(longName);
        Set<ConstraintViolation<CourseCategory>> violations = validator.validate(courseCategory);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Description - should handle long descriptions")
    void testDescription() {
        String longDescription = "a".repeat(1000);
        courseCategory.setDescription(longDescription);
        assertEquals(longDescription, courseCategory.getDescription());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        courseCategory.setStatus(1);
        courseCategory.setIsDeleted(0);
        assertEquals(1, courseCategory.getStatus());
        assertEquals(0, courseCategory.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        courseCategory.setCreatedAt(now);
        courseCategory.setUpdatedAt(later);

        assertEquals(now, courseCategory.getCreatedAt());
        assertEquals(later, courseCategory.getUpdatedAt());
    }
}

