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

@DisplayName("Slide Entity Tests")
class SlideTest {

    private Validator validator;
    private Slide slide;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        slide = new Slide();
    }

    @Test
    @DisplayName("Test Slide NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(slide);
        assertNull(slide.getId());
        assertNull(slide.getTitle());
    }

    @Test
    @DisplayName("Test Slide AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String title = "Slide Title";
        String description = "Slide description";
        String imageUrl = "https://example.com/image.jpg";
        String linkUrl = "https://example.com";
        Integer orderIndex = 1;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Slide slide = new Slide(id, title, description, imageUrl, linkUrl, orderIndex, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, slide.getId());
        assertEquals(title, slide.getTitle());
        assertEquals(description, slide.getDescription());
        assertEquals(imageUrl, slide.getImageUrl());
        assertEquals(linkUrl, slide.getLinkUrl());
        assertEquals(orderIndex, slide.getOrderIndex());
    }

    @Test
    @DisplayName("Test Slide Builder")
    void testBuilder() {
        String title = "Promo Slide";
        String imageUrl = "https://example.com/promo.jpg";

        Slide slide = Slide.builder()
                .title(title)
                .imageUrl(imageUrl)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(title, slide.getTitle());
        assertEquals(imageUrl, slide.getImageUrl());
    }

    @Test
    @DisplayName("Test Slide Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String title = "Test Slide";
        String description = "Test Description";
        String imageUrl = "https://example.com/test.jpg";
        String linkUrl = "https://example.com/link";
        Integer orderIndex = 1;

        slide.setId(id);
        slide.setTitle(title);
        slide.setDescription(description);
        slide.setImageUrl(imageUrl);
        slide.setLinkUrl(linkUrl);
        slide.setOrderIndex(orderIndex);

        assertEquals(id, slide.getId());
        assertEquals(title, slide.getTitle());
        assertEquals(description, slide.getDescription());
        assertEquals(imageUrl, slide.getImageUrl());
        assertEquals(linkUrl, slide.getLinkUrl());
        assertEquals(orderIndex, slide.getOrderIndex());
    }

    @Test
    @DisplayName("Test Title validation - should accept valid title")
    void testTitleValidation_Valid() {
        slide.setTitle("Valid Title");
        slide.setImageUrl("https://example.com/image.jpg");
        Set<ConstraintViolation<Slide>> violations = validator.validate(slide);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Title validation - should reject title exceeding max length")
    void testTitleValidation_ExceedsMaxLength() {
        slide.setImageUrl("https://example.com/image.jpg");
        String longTitle = "a".repeat(256); // 256 characters, max is 255
        slide.setTitle(longTitle);
        Set<ConstraintViolation<Slide>> violations = validator.validate(slide);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test ImageUrl validation - should reject null imageUrl")
    void testImageUrlValidation_Null() {
        slide.setTitle("Test");
        slide.setImageUrl(null);
        Set<ConstraintViolation<Slide>> violations = validator.validate(slide);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("imageUrl")));
    }

    @Test
    @DisplayName("Test ImageUrl validation - should accept valid imageUrl")
    void testImageUrlValidation_Valid() {
        slide.setTitle("Test");
        slide.setImageUrl("https://example.com/image.jpg");
        Set<ConstraintViolation<Slide>> violations = validator.validate(slide);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Description - should handle long descriptions")
    void testDescription() {
        String longDescription = "a".repeat(1000);
        slide.setImageUrl("https://example.com/image.jpg");
        slide.setDescription(longDescription);
        assertEquals(longDescription, slide.getDescription());
    }

    @Test
    @DisplayName("Test OrderIndex - should handle order index values")
    void testOrderIndex() {
        slide.setImageUrl("https://example.com/image.jpg");
        slide.setOrderIndex(0);
        assertEquals(0, slide.getOrderIndex());

        slide.setOrderIndex(10);
        assertEquals(10, slide.getOrderIndex());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        slide.setStatus(1);
        slide.setIsDeleted(0);
        assertEquals(1, slide.getStatus());
        assertEquals(0, slide.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        slide.setCreatedAt(now);
        slide.setUpdatedAt(later);

        assertEquals(now, slide.getCreatedAt());
        assertEquals(later, slide.getUpdatedAt());
    }
}

