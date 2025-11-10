package com.devmam.taraacademyapi.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuizOption Entity Tests")
class QuizOptionTest {

    private QuizOption quizOption;

    @BeforeEach
    void setUp() {
        quizOption = new QuizOption();
    }

    @Test
    @DisplayName("Test QuizOption NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(quizOption);
        assertNull(quizOption.getId());
        assertNull(quizOption.getContent());
    }

    @Test
    @DisplayName("Test QuizOption AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String content = "Option A";
        Boolean isCorrect = true;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        QuizOption quizOption = new QuizOption(id, null, content, isCorrect, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, quizOption.getId());
        assertEquals(content, quizOption.getContent());
        assertEquals(isCorrect, quizOption.getIsCorrect());
    }

    @Test
    @DisplayName("Test QuizOption Builder")
    void testBuilder() {
        String content = "Option B";
        Boolean isCorrect = false;

        QuizOption quizOption = QuizOption.builder()
                .content(content)
                .isCorrect(isCorrect)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(content, quizOption.getContent());
        assertEquals(isCorrect, quizOption.getIsCorrect());
    }

    @Test
    @DisplayName("Test QuizOption Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String content = "Option C";
        Boolean isCorrect = true;

        quizOption.setId(id);
        quizOption.setContent(content);
        quizOption.setIsCorrect(isCorrect);

        assertEquals(id, quizOption.getId());
        assertEquals(content, quizOption.getContent());
        assertEquals(isCorrect, quizOption.getIsCorrect());
    }

    @Test
    @DisplayName("Test Content - should handle long content")
    void testContent() {
        String longContent = "a".repeat(1000);
        quizOption.setContent(longContent);
        assertEquals(longContent, quizOption.getContent());
    }

    @Test
    @DisplayName("Test IsCorrect - should handle boolean values with default false")
    void testIsCorrect() {
        quizOption.setIsCorrect(true);
        assertTrue(quizOption.getIsCorrect());

        quizOption.setIsCorrect(false);
        assertFalse(quizOption.getIsCorrect());

        quizOption.setIsCorrect(null);
        assertNull(quizOption.getIsCorrect());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        quizOption.setStatus(1);
        quizOption.setIsDeleted(0);
        assertEquals(1, quizOption.getStatus());
        assertEquals(0, quizOption.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        quizOption.setCreatedAt(now);
        quizOption.setUpdatedAt(later);

        assertEquals(now, quizOption.getCreatedAt());
        assertEquals(later, quizOption.getUpdatedAt());
    }
}

