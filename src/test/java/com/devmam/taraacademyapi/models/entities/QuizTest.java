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

@DisplayName("Quiz Entity Tests")
class QuizTest {

    private Validator validator;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        quiz = new Quiz();
    }

    @Test
    @DisplayName("Test Quiz NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(quiz);
        assertNull(quiz.getId());
        assertNull(quiz.getType());
    }

    @Test
    @DisplayName("Test Quiz AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String type = "MULTIPLE_CHOICE";
        String question = "What is Java?";
        String answer = "A programming language";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Quiz quiz = new Quiz(id, null, type, question, answer, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, quiz.getId());
        assertEquals(type, quiz.getType());
        assertEquals(question, quiz.getQuestion());
        assertEquals(answer, quiz.getAnswer());
    }

    @Test
    @DisplayName("Test Quiz Builder")
    void testBuilder() {
        String type = "TRUE_FALSE";
        String question = "Is Java object-oriented?";

        Quiz quiz = Quiz.builder()
                .type(type)
                .question(question)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(type, quiz.getType());
        assertEquals(question, quiz.getQuestion());
    }

    @Test
    @DisplayName("Test Quiz Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String type = "MULTIPLE_CHOICE";
        String question = "Test question?";
        String answer = "Test answer";

        quiz.setId(id);
        quiz.setType(type);
        quiz.setQuestion(question);
        quiz.setAnswer(answer);

        assertEquals(id, quiz.getId());
        assertEquals(type, quiz.getType());
        assertEquals(question, quiz.getQuestion());
        assertEquals(answer, quiz.getAnswer());
    }

    @Test
    @DisplayName("Test Type validation - should accept valid type")
    void testTypeValidation_Valid() {
        quiz.setType("MULTIPLE_CHOICE");
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Type validation - should reject type exceeding max length")
    void testTypeValidation_ExceedsMaxLength() {
        String longType = "a".repeat(51); // 51 characters, max is 50
        quiz.setType(longType);
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Question - should handle long questions")
    void testQuestion() {
        String longQuestion = "a".repeat(1000);
        quiz.setQuestion(longQuestion);
        assertEquals(longQuestion, quiz.getQuestion());
    }

    @Test
    @DisplayName("Test Answer - should handle long answers")
    void testAnswer() {
        String longAnswer = "a".repeat(1000);
        quiz.setAnswer(longAnswer);
        assertEquals(longAnswer, quiz.getAnswer());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        quiz.setStatus(1);
        quiz.setIsDeleted(0);
        assertEquals(1, quiz.getStatus());
        assertEquals(0, quiz.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        quiz.setCreatedAt(now);
        quiz.setUpdatedAt(later);

        assertEquals(now, quiz.getCreatedAt());
        assertEquals(later, quiz.getUpdatedAt());
    }
}

