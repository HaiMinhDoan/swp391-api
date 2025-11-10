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

@DisplayName("Feedback Entity Tests")
class FeedbackTest {

    private Validator validator;
    private Feedback feedback;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        feedback = new Feedback();
    }

    @Test
    @DisplayName("Test Feedback NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(feedback);
        assertNull(feedback.getId());
        assertNull(feedback.getReferenceType());
    }

    @Test
    @DisplayName("Test Feedback AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String referenceType = "COURSE";
        Integer referenceId = 100;
        Integer rating = 5;
        String comment = "Great course!";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Feedback feedback = new Feedback(id, null, referenceType, referenceId, rating, comment, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, feedback.getId());
        assertEquals(referenceType, feedback.getReferenceType());
        assertEquals(referenceId, feedback.getReferenceId());
        assertEquals(rating, feedback.getRating());
        assertEquals(comment, feedback.getComment());
    }

    @Test
    @DisplayName("Test Feedback Builder")
    void testBuilder() {
        String referenceType = "LESSON";
        Integer referenceId = 200;
        Integer rating = 4;

        Feedback feedback = Feedback.builder()
                .referenceType(referenceType)
                .referenceId(referenceId)
                .rating(rating)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(referenceType, feedback.getReferenceType());
        assertEquals(referenceId, feedback.getReferenceId());
        assertEquals(rating, feedback.getRating());
    }

    @Test
    @DisplayName("Test Feedback Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String referenceType = "COURSE";
        Integer referenceId = 100;
        Integer rating = 5;
        String comment = "Excellent!";

        feedback.setId(id);
        feedback.setReferenceType(referenceType);
        feedback.setReferenceId(referenceId);
        feedback.setRating(rating);
        feedback.setComment(comment);

        assertEquals(id, feedback.getId());
        assertEquals(referenceType, feedback.getReferenceType());
        assertEquals(referenceId, feedback.getReferenceId());
        assertEquals(rating, feedback.getRating());
        assertEquals(comment, feedback.getComment());
    }

    @Test
    @DisplayName("Test ReferenceType validation - should accept valid reference type")
    void testReferenceTypeValidation_Valid() {
        feedback.setReferenceType("COURSE");
        Set<ConstraintViolation<Feedback>> violations = validator.validate(feedback);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test ReferenceType validation - should reject reference type exceeding max length")
    void testReferenceTypeValidation_ExceedsMaxLength() {
        String longReferenceType = "a".repeat(101); // 101 characters, max is 100
        feedback.setReferenceType(longReferenceType);
        Set<ConstraintViolation<Feedback>> violations = validator.validate(feedback);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Rating - should handle rating values")
    void testRating() {
        feedback.setRating(1);
        assertEquals(1, feedback.getRating());

        feedback.setRating(5);
        assertEquals(5, feedback.getRating());

        feedback.setRating(3);
        assertEquals(3, feedback.getRating());
    }

    @Test
    @DisplayName("Test Comment - should handle long comments")
    void testComment() {
        String longComment = "a".repeat(1000);
        feedback.setComment(longComment);
        assertEquals(longComment, feedback.getComment());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        feedback.setStatus(1);
        feedback.setIsDeleted(0);
        assertEquals(1, feedback.getStatus());
        assertEquals(0, feedback.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        feedback.setCreatedAt(now);
        feedback.setUpdatedAt(later);

        assertEquals(now, feedback.getCreatedAt());
        assertEquals(later, feedback.getUpdatedAt());
    }
}

