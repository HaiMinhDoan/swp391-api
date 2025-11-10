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

@DisplayName("Course Entity Tests")
class CourseTest {

    private Validator validator;
    private Course course;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        course = new Course();
    }

    @Test
    @DisplayName("Test Course NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(course);
        assertNull(course.getId());
        assertNull(course.getTitle());
    }

    @Test
    @DisplayName("Test Course Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String title = "Java Programming";
        String summary = "Learn Java";
        String description = "Complete Java course";
        String lang = "en";
        BigDecimal price = new BigDecimal("99.99");
        Integer saleOff = 10;
        String thumbnail = "https://example.com/thumb.jpg";

        course.setId(id);
        course.setTitle(title);
        course.setSummary(summary);
        course.setDescription(description);
        course.setLang(lang);
        course.setPrice(price);
        course.setSaleOff(saleOff);
        course.setThumbnail(thumbnail);
        course.setStatus(1);
        course.setIsDeleted(0);

        assertEquals(id, course.getId());
        assertEquals(title, course.getTitle());
        assertEquals(summary, course.getSummary());
        assertEquals(description, course.getDescription());
        assertEquals(lang, course.getLang());
        assertEquals(price, course.getPrice());
        assertEquals(saleOff, course.getSaleOff());
        assertEquals(thumbnail, course.getThumbnail());
        assertEquals(1, course.getStatus());
        assertEquals(0, course.getIsDeleted());
    }

    @Test
    @DisplayName("Test Title validation - should accept valid title")
    void testTitleValidation_Valid() {
        course.setTitle("Valid Title");
        Set<ConstraintViolation<Course>> violations = validator.validate(course);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Title validation - should reject title exceeding max length")
    void testTitleValidation_ExceedsMaxLength() {
        String longTitle = "a".repeat(256); // 256 characters, max is 255
        course.setTitle(longTitle);
        Set<ConstraintViolation<Course>> violations = validator.validate(course);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Summary validation - should accept valid summary")
    void testSummaryValidation_Valid() {
        course.setSummary("Valid Summary");
        Set<ConstraintViolation<Course>> violations = validator.validate(course);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Lang validation - should accept valid lang")
    void testLangValidation_Valid() {
        course.setLang("en");
        Set<ConstraintViolation<Course>> violations = validator.validate(course);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Price - should handle BigDecimal values")
    void testPrice() {
        BigDecimal price1 = new BigDecimal("0.00");
        BigDecimal price2 = new BigDecimal("999.99");
        BigDecimal price3 = new BigDecimal("1234.56");

        course.setPrice(price1);
        assertEquals(price1, course.getPrice());

        course.setPrice(price2);
        assertEquals(price2, course.getPrice());

        course.setPrice(price3);
        assertEquals(price3, course.getPrice());
    }

    @Test
    @DisplayName("Test SaleOff - should handle sale off values")
    void testSaleOff() {
        course.setSaleOff(0);
        assertEquals(0, course.getSaleOff());

        course.setSaleOff(50);
        assertEquals(50, course.getSaleOff());

        course.setSaleOff(100);
        assertEquals(100, course.getSaleOff());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        course.setStatus(1);
        course.setIsDeleted(0);
        assertEquals(1, course.getStatus());
        assertEquals(0, course.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        course.setCreatedAt(now);
        course.setUpdatedAt(later);

        assertEquals(now, course.getCreatedAt());
        assertEquals(later, course.getUpdatedAt());
    }
}

