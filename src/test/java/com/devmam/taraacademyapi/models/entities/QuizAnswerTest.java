package com.devmam.taraacademyapi.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuizAnswer Entity Tests")
class QuizAnswerTest {

    private QuizAnswer quizAnswer;

    @BeforeEach
    void setUp() {
        quizAnswer = new QuizAnswer();
    }

    @Test
    @DisplayName("Test QuizAnswer NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(quizAnswer);
        assertNull(quizAnswer.getId());
        assertNull(quizAnswer.getSelectedOptionId());
    }

    @Test
    @DisplayName("Test QuizAnswer AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        Integer selectedOptionId = 5;
        String answerText = "Selected answer";
        Boolean isCorrect = true;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        QuizAnswer quizAnswer = new QuizAnswer(id, null, null, selectedOptionId, answerText, isCorrect, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, quizAnswer.getId());
        assertEquals(selectedOptionId, quizAnswer.getSelectedOptionId());
        assertEquals(answerText, quizAnswer.getAnswerText());
        assertEquals(isCorrect, quizAnswer.getIsCorrect());
    }

    @Test
    @DisplayName("Test QuizAnswer Builder")
    void testBuilder() {
        Integer selectedOptionId = 3;
        String answerText = "Answer text";
        Boolean isCorrect = false;

        QuizAnswer quizAnswer = QuizAnswer.builder()
                .selectedOptionId(selectedOptionId)
                .answerText(answerText)
                .isCorrect(isCorrect)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(selectedOptionId, quizAnswer.getSelectedOptionId());
        assertEquals(answerText, quizAnswer.getAnswerText());
        assertEquals(isCorrect, quizAnswer.getIsCorrect());
    }

    @Test
    @DisplayName("Test QuizAnswer Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        Integer selectedOptionId = 2;
        String answerText = "Test answer";
        Boolean isCorrect = true;

        quizAnswer.setId(id);
        quizAnswer.setSelectedOptionId(selectedOptionId);
        quizAnswer.setAnswerText(answerText);
        quizAnswer.setIsCorrect(isCorrect);

        assertEquals(id, quizAnswer.getId());
        assertEquals(selectedOptionId, quizAnswer.getSelectedOptionId());
        assertEquals(answerText, quizAnswer.getAnswerText());
        assertEquals(isCorrect, quizAnswer.getIsCorrect());
    }

    @Test
    @DisplayName("Test SelectedOptionId - should handle option IDs")
    void testSelectedOptionId() {
        quizAnswer.setSelectedOptionId(1);
        assertEquals(1, quizAnswer.getSelectedOptionId());

        quizAnswer.setSelectedOptionId(10);
        assertEquals(10, quizAnswer.getSelectedOptionId());
    }

    @Test
    @DisplayName("Test AnswerText - should handle long answer text")
    void testAnswerText() {
        String longAnswerText = "a".repeat(1000);
        quizAnswer.setAnswerText(longAnswerText);
        assertEquals(longAnswerText, quizAnswer.getAnswerText());
    }

    @Test
    @DisplayName("Test IsCorrect - should handle boolean values")
    void testIsCorrect() {
        quizAnswer.setIsCorrect(true);
        assertTrue(quizAnswer.getIsCorrect());

        quizAnswer.setIsCorrect(false);
        assertFalse(quizAnswer.getIsCorrect());

        quizAnswer.setIsCorrect(null);
        assertNull(quizAnswer.getIsCorrect());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        quizAnswer.setStatus(1);
        quizAnswer.setIsDeleted(0);
        assertEquals(1, quizAnswer.getStatus());
        assertEquals(0, quizAnswer.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        quizAnswer.setCreatedAt(now);
        quizAnswer.setUpdatedAt(later);

        assertEquals(now, quizAnswer.getCreatedAt());
        assertEquals(later, quizAnswer.getUpdatedAt());
    }
}

