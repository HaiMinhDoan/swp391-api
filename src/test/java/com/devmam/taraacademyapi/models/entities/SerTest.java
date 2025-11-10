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

@DisplayName("Ser Entity Tests")
class SerTest {

    private Validator validator;
    private Ser ser;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        ser = new Ser();
    }

    @Test
    @DisplayName("Test Ser NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(ser);
        assertNull(ser.getId());
        assertNull(ser.getName());
    }

    @Test
    @DisplayName("Test Ser AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String name = "Consulting Service";
        String description = "Service description";
        String detail = "Service details";
        BigDecimal price = new BigDecimal("500.00");
        String thumnail = "https://example.com/thumb.jpg";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Ser ser = new Ser(id, name, description, detail, price, thumnail, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, ser.getId());
        assertEquals(name, ser.getName());
        assertEquals(description, ser.getDescription());
        assertEquals(detail, ser.getDetail());
        assertEquals(price, ser.getPrice());
        assertEquals(thumnail, ser.getThumnail());
    }

    @Test
    @DisplayName("Test Ser Builder")
    void testBuilder() {
        String name = "Training Service";
        BigDecimal price = new BigDecimal("1000.00");

        Ser ser = Ser.builder()
                .name(name)
                .price(price)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(name, ser.getName());
        assertEquals(price, ser.getPrice());
    }

    @Test
    @DisplayName("Test Ser Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String name = "Service Name";
        String description = "Service Description";
        String detail = "Service Detail";
        BigDecimal price = new BigDecimal("750.00");
        String thumnail = "thumb.jpg";

        ser.setId(id);
        ser.setName(name);
        ser.setDescription(description);
        ser.setDetail(detail);
        ser.setPrice(price);
        ser.setThumnail(thumnail);

        assertEquals(id, ser.getId());
        assertEquals(name, ser.getName());
        assertEquals(description, ser.getDescription());
        assertEquals(detail, ser.getDetail());
        assertEquals(price, ser.getPrice());
        assertEquals(thumnail, ser.getThumnail());
    }

    @Test
    @DisplayName("Test Name validation - should accept valid name")
    void testNameValidation_Valid() {
        ser.setName("Valid Service Name");
        Set<ConstraintViolation<Ser>> violations = validator.validate(ser);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Name validation - should reject name exceeding max length")
    void testNameValidation_ExceedsMaxLength() {
        String longName = "a".repeat(256); // 256 characters, max is 255
        ser.setName(longName);
        Set<ConstraintViolation<Ser>> violations = validator.validate(ser);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Thumnail validation - should accept valid thumbnail")
    void testThumnailValidation_Valid() {
        ser.setName("Service");
        ser.setThumnail("thumb.jpg");
        Set<ConstraintViolation<Ser>> violations = validator.validate(ser);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Thumnail validation - should reject thumbnail exceeding max length")
    void testThumnailValidation_ExceedsMaxLength() {
        ser.setName("Service");
        String longThumnail = "a".repeat(556); // 556 characters, max is 555
        ser.setThumnail(longThumnail);
        Set<ConstraintViolation<Ser>> violations = validator.validate(ser);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Price - should handle BigDecimal values")
    void testPrice() {
        BigDecimal price1 = new BigDecimal("0.00");
        BigDecimal price2 = new BigDecimal("9999.99");
        BigDecimal price3 = new BigDecimal("1234.56");

        ser.setPrice(price1);
        assertEquals(price1, ser.getPrice());

        ser.setPrice(price2);
        assertEquals(price2, ser.getPrice());

        ser.setPrice(price3);
        assertEquals(price3, ser.getPrice());
    }

    @Test
    @DisplayName("Test Description and Detail - should handle long text")
    void testDescriptionAndDetail() {
        String longDescription = "a".repeat(1000);
        String longDetail = "b".repeat(2000);

        ser.setDescription(longDescription);
        ser.setDetail(longDetail);

        assertEquals(longDescription, ser.getDescription());
        assertEquals(longDetail, ser.getDetail());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        ser.setStatus(1);
        ser.setIsDeleted(0);
        assertEquals(1, ser.getStatus());
        assertEquals(0, ser.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        ser.setCreatedAt(now);
        ser.setUpdatedAt(later);

        assertEquals(now, ser.getCreatedAt());
        assertEquals(later, ser.getUpdatedAt());
    }
}

