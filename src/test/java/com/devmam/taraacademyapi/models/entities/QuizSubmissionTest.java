package com.devmam.taraacademyapi.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuizSubmission Entity Tests")
class QuizSubmissionTest {

    private QuizSubmission quizSubmission;

    @BeforeEach
    void setUp() {
        quizSubmission = new QuizSubmission();
    }

    @Test
    @DisplayName("Test QuizSubmission NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(quizSubmission);
        assertNull(quizSubmission.getId());
        assertNull(quizSubmission.getScore());
    }

    @Test
    @DisplayName("Test QuizSubmission AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        Instant startedAt = Instant.now();
        Instant submittedAt = Instant.now().plusSeconds(3600);
        BigDecimal score = new BigDecimal("85.50");
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        QuizSubmission quizSubmission = new QuizSubmission(id, null, null, startedAt, submittedAt, score, createdAt, updatedAt, status, isDeleted, null);

        assertEquals(id, quizSubmission.getId());
        assertEquals(startedAt, quizSubmission.getStartedAt());
        assertEquals(submittedAt, quizSubmission.getSubmittedAt());
        assertEquals(score, quizSubmission.getScore());
    }

    @Test
    @DisplayName("Test QuizSubmission Builder")
    void testBuilder() {
        BigDecimal score = new BigDecimal("90.00");
        Instant startedAt = Instant.now();

        QuizSubmission quizSubmission = QuizSubmission.builder()
                .score(score)
                .startedAt(startedAt)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(score, quizSubmission.getScore());
        assertEquals(startedAt, quizSubmission.getStartedAt());
    }

    @Test
    @DisplayName("Test QuizSubmission Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        Instant startedAt = Instant.now();
        Instant submittedAt = Instant.now().plusSeconds(1800);
        BigDecimal score = new BigDecimal("75.25");

        quizSubmission.setId(id);
        quizSubmission.setStartedAt(startedAt);
        quizSubmission.setSubmittedAt(submittedAt);
        quizSubmission.setScore(score);

        assertEquals(id, quizSubmission.getId());
        assertEquals(startedAt, quizSubmission.getStartedAt());
        assertEquals(submittedAt, quizSubmission.getSubmittedAt());
        assertEquals(score, quizSubmission.getScore());
    }

    @Test
    @DisplayName("Test Score - should handle BigDecimal values")
    void testScore() {
        BigDecimal score1 = new BigDecimal("0.00");
        BigDecimal score2 = new BigDecimal("100.00");
        BigDecimal score3 = new BigDecimal("85.75");

        quizSubmission.setScore(score1);
        assertEquals(score1, quizSubmission.getScore());

        quizSubmission.setScore(score2);
        assertEquals(score2, quizSubmission.getScore());

        quizSubmission.setScore(score3);
        assertEquals(score3, quizSubmission.getScore());
    }

    @Test
    @DisplayName("Test StartedAt and SubmittedAt - should handle timestamps")
    void testTimestamps() {
        Instant startedAt = Instant.now();
        Instant submittedAt = startedAt.plusSeconds(3600);

        quizSubmission.setStartedAt(startedAt);
        quizSubmission.setSubmittedAt(submittedAt);

        assertEquals(startedAt, quizSubmission.getStartedAt());
        assertEquals(submittedAt, quizSubmission.getSubmittedAt());
        assertTrue(quizSubmission.getSubmittedAt().isAfter(quizSubmission.getStartedAt()));
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        quizSubmission.setStatus(1);
        quizSubmission.setIsDeleted(0);
        assertEquals(1, quizSubmission.getStatus());
        assertEquals(0, quizSubmission.getIsDeleted());
    }

    @Test
    @DisplayName("Test CreatedAt and UpdatedAt")
    void testCreatedAndUpdatedAt() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        quizSubmission.setCreatedAt(now);
        quizSubmission.setUpdatedAt(later);

        assertEquals(now, quizSubmission.getCreatedAt());
        assertEquals(later, quizSubmission.getUpdatedAt());
    }
}

