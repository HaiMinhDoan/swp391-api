package com.devmam.taraacademyapi.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserCourse Entity Tests")
class UserCourseTest {

    private Validator validator;
    private UserCourse userCourse;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userCourse = new UserCourse();
    }

    @Test
    @DisplayName("Test UserCourse NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(userCourse);
        assertNull(userCourse.getId());
        assertNull(userCourse.getExpiredAt());
    }

    @Test
    @DisplayName("Test UserCourse AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        Instant enrolledAt = Instant.now();
        Instant expiredAt = Instant.now().plusSeconds(86400 * 365); // 1 year
        Instant completedAt = null;
        BigDecimal progress = new BigDecimal("0.00");
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer currentLessonId = 1;
        Integer currentStageId = 1;
        Integer status = 1;
        Integer isDeleted = 0;

        UserCourse userCourse = new UserCourse(id, null, null, null, enrolledAt, expiredAt, completedAt, progress, createdAt, updatedAt, currentLessonId, currentStageId, status, isDeleted);

        assertEquals(id, userCourse.getId());
        assertEquals(enrolledAt, userCourse.getEnrolledAt());
        assertEquals(expiredAt, userCourse.getExpiredAt());
        assertEquals(completedAt, userCourse.getCompletedAt());
        assertEquals(progress, userCourse.getProgress());
        assertEquals(currentLessonId, userCourse.getCurrentLessonId());
        assertEquals(currentStageId, userCourse.getCurrentStageId());
    }

    @Test
    @DisplayName("Test UserCourse Builder")
    void testBuilder() {
        Instant expiredAt = Instant.now().plusSeconds(86400 * 180); // 6 months
        BigDecimal progress = new BigDecimal("25.50");

        UserCourse userCourse = UserCourse.builder()
                .expiredAt(expiredAt)
                .progress(progress)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(expiredAt, userCourse.getExpiredAt());
        assertEquals(progress, userCourse.getProgress());
    }

    @Test
    @DisplayName("Test UserCourse Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        Instant enrolledAt = Instant.now();
        Instant expiredAt = Instant.now().plusSeconds(86400 * 365);
        Instant completedAt = Instant.now().plusSeconds(86400 * 200);
        BigDecimal progress = new BigDecimal("50.00");
        Integer currentLessonId = 5;
        Integer currentStageId = 2;

        userCourse.setId(id);
        userCourse.setEnrolledAt(enrolledAt);
        userCourse.setExpiredAt(expiredAt);
        userCourse.setCompletedAt(completedAt);
        userCourse.setProgress(progress);
        userCourse.setCurrentLessonId(currentLessonId);
        userCourse.setCurrentStageId(currentStageId);

        assertEquals(id, userCourse.getId());
        assertEquals(enrolledAt, userCourse.getEnrolledAt());
        assertEquals(expiredAt, userCourse.getExpiredAt());
        assertEquals(completedAt, userCourse.getCompletedAt());
        assertEquals(progress, userCourse.getProgress());
        assertEquals(currentLessonId, userCourse.getCurrentLessonId());
        assertEquals(currentStageId, userCourse.getCurrentStageId());
    }

    @Test
    @DisplayName("Test ExpiredAt validation - should reject null expiredAt")
    void testExpiredAtValidation_Null() {
        userCourse.setExpiredAt(null);
        Set<ConstraintViolation<UserCourse>> violations = validator.validate(userCourse);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("expiredAt")));
    }

    @Test
    @DisplayName("Test ExpiredAt validation - should accept valid expiredAt")
    void testExpiredAtValidation_Valid() {
        userCourse.setExpiredAt(Instant.now().plusSeconds(86400));
        Set<ConstraintViolation<UserCourse>> violations = validator.validate(userCourse);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Progress - should handle BigDecimal values")
    void testProgress() {
        BigDecimal progress1 = new BigDecimal("0.00");
        BigDecimal progress2 = new BigDecimal("100.00");
        BigDecimal progress3 = new BigDecimal("75.50");

        userCourse.setProgress(progress1);
        assertEquals(progress1, userCourse.getProgress());

        userCourse.setProgress(progress2);
        assertEquals(progress2, userCourse.getProgress());

        userCourse.setProgress(progress3);
        assertEquals(progress3, userCourse.getProgress());
    }

    @Test
    @DisplayName("Test CurrentLessonId and CurrentStageId - should handle IDs")
    void testCurrentIds() {
        userCourse.setCurrentLessonId(1);
        userCourse.setCurrentStageId(1);
        assertEquals(1, userCourse.getCurrentLessonId());
        assertEquals(1, userCourse.getCurrentStageId());

        userCourse.setCurrentLessonId(10);
        userCourse.setCurrentStageId(5);
        assertEquals(10, userCourse.getCurrentLessonId());
        assertEquals(5, userCourse.getCurrentStageId());
    }

    @Test
    @DisplayName("Test EnrolledAt, ExpiredAt, CompletedAt - should handle timestamps")
    void testTimestamps() {
        Instant enrolledAt = Instant.now();
        Instant expiredAt = enrolledAt.plusSeconds(86400 * 365);
        Instant completedAt = enrolledAt.plusSeconds(86400 * 200);

        userCourse.setEnrolledAt(enrolledAt);
        userCourse.setExpiredAt(expiredAt);
        userCourse.setCompletedAt(completedAt);

        assertEquals(enrolledAt, userCourse.getEnrolledAt());
        assertEquals(expiredAt, userCourse.getExpiredAt());
        assertEquals(completedAt, userCourse.getCompletedAt());
        assertTrue(userCourse.getExpiredAt().isAfter(userCourse.getEnrolledAt()));
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        userCourse.setStatus(1);
        userCourse.setIsDeleted(0);
        assertEquals(1, userCourse.getStatus());
        assertEquals(0, userCourse.getIsDeleted());
    }

    @Test
    @DisplayName("Test CreatedAt and UpdatedAt")
    void testCreatedAndUpdatedAt() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        userCourse.setCreatedAt(now);
        userCourse.setUpdatedAt(later);

        assertEquals(now, userCourse.getCreatedAt());
        assertEquals(later, userCourse.getUpdatedAt());
    }
}

