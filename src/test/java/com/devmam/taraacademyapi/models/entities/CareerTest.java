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

@DisplayName("Career Entity Tests")
class CareerTest {

    private Validator validator;
    private Career career;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        career = new Career();
    }

    @Test
    @DisplayName("Test Career NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(career);
        assertNull(career.getId());
        assertNull(career.getTitle());
    }

    @Test
    @DisplayName("Test Career AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String title = "Software Engineer";
        String description = "Job description";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Career career = new Career(id, title, description, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, career.getId());
        assertEquals(title, career.getTitle());
        assertEquals(description, career.getDescription());
        assertEquals(createdAt, career.getCreatedAt());
        assertEquals(updatedAt, career.getUpdatedAt());
        assertEquals(status, career.getStatus());
        assertEquals(isDeleted, career.getIsDeleted());
    }

    @Test
    @DisplayName("Test Career Builder")
    void testBuilder() {
        String title = "Data Scientist";
        String description = "Data science position";

        Career career = Career.builder()
                .title(title)
                .description(description)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(title, career.getTitle());
        assertEquals(description, career.getDescription());
        assertEquals(1, career.getStatus());
        assertEquals(0, career.getIsDeleted());
    }

    @Test
    @DisplayName("Test Career Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String title = "Product Manager";
        String description = "Product management role";

        career.setId(id);
        career.setTitle(title);
        career.setDescription(description);
        career.setStatus(1);
        career.setIsDeleted(0);

        assertEquals(id, career.getId());
        assertEquals(title, career.getTitle());
        assertEquals(description, career.getDescription());
        assertEquals(1, career.getStatus());
        assertEquals(0, career.getIsDeleted());
    }

    @Test
    @DisplayName("Test Title validation - should accept valid title")
    void testTitleValidation_Valid() {
        career.setTitle("Valid Title");
        Set<ConstraintViolation<Career>> violations = validator.validate(career);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Title validation - should reject title exceeding max length")
    void testTitleValidation_ExceedsMaxLength() {
        String longTitle = "a".repeat(256); // 256 characters, max is 255
        career.setTitle(longTitle);
        Set<ConstraintViolation<Career>> violations = validator.validate(career);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Description - should handle long descriptions")
    void testDescription() {
        String longDescription = "a".repeat(1000);
        career.setDescription(longDescription);
        assertEquals(longDescription, career.getDescription());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        career.setStatus(1);
        career.setIsDeleted(0);
        assertEquals(1, career.getStatus());
        assertEquals(0, career.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        career.setCreatedAt(now);
        career.setUpdatedAt(later);

        assertEquals(now, career.getCreatedAt());
        assertEquals(later, career.getUpdatedAt());
    }
}

