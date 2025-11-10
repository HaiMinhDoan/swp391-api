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

@DisplayName("Booking Entity Tests")
class BookingTest {

    private Validator validator;
    private Booking booking;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        booking = new Booking();
    }

    @Test
    @DisplayName("Test Booking NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(booking);
        assertNull(booking.getId());
        assertNull(booking.getAccountId());
    }

    @Test
    @DisplayName("Test Booking AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        Integer accountId = 100;
        String fullName = "John Doe";
        String phone = "0123456789";
        String email = "john@example.com";
        String company = "Test Company";
        String personalRole = "Manager";
        String message = "Test message";
        String note = "Test note";
        Instant bookingDate = Instant.now();
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Booking booking = new Booking(id, accountId, null, bookingDate, note, fullName, phone, email, company, personalRole, message, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, booking.getId());
        assertEquals(accountId, booking.getAccountId());
        assertEquals(fullName, booking.getFullName());
        assertEquals(phone, booking.getPhone());
        assertEquals(email, booking.getEmail());
        assertEquals(company, booking.getCompany());
        assertEquals(personalRole, booking.getPersonalRole());
        assertEquals(message, booking.getMessage());
        assertEquals(note, booking.getNote());
        assertEquals(bookingDate, booking.getBookingDate());
    }

    @Test
    @DisplayName("Test Booking Builder")
    void testBuilder() {
        Integer accountId = 200;
        String fullName = "Jane Doe";
        String email = "jane@example.com";

        Booking booking = Booking.builder()
                .accountId(accountId)
                .fullName(fullName)
                .email(email)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(accountId, booking.getAccountId());
        assertEquals(fullName, booking.getFullName());
        assertEquals(email, booking.getEmail());
    }

    @Test
    @DisplayName("Test Booking Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        Integer accountId = 100;
        String fullName = "Test User";
        String phone = "0987654321";
        String email = "test@example.com";
        String company = "Company";
        String personalRole = "Role";
        String message = "Message";
        String note = "Note";
        Instant bookingDate = Instant.now();

        booking.setId(id);
        booking.setAccountId(accountId);
        booking.setFullName(fullName);
        booking.setPhone(phone);
        booking.setEmail(email);
        booking.setCompany(company);
        booking.setPersonalRole(personalRole);
        booking.setMessage(message);
        booking.setNote(note);
        booking.setBookingDate(bookingDate);

        assertEquals(id, booking.getId());
        assertEquals(accountId, booking.getAccountId());
        assertEquals(fullName, booking.getFullName());
        assertEquals(phone, booking.getPhone());
        assertEquals(email, booking.getEmail());
        assertEquals(company, booking.getCompany());
        assertEquals(personalRole, booking.getPersonalRole());
        assertEquals(message, booking.getMessage());
        assertEquals(note, booking.getNote());
        assertEquals(bookingDate, booking.getBookingDate());
    }

    @Test
    @DisplayName("Test Note validation - should accept valid note")
    void testNoteValidation_Valid() {
        booking.setNote("Valid note");
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Note validation - should reject note exceeding max length")
    void testNoteValidation_ExceedsMaxLength() {
        String longNote = "a".repeat(401); // 401 characters, max is 400
        booking.setNote(longNote);
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test FullName validation - should accept valid full name")
    void testFullNameValidation_Valid() {
        booking.setFullName("John Doe");
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Phone validation - should accept valid phone")
    void testPhoneValidation_Valid() {
        booking.setPhone("0123456789");
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Email validation - should accept valid email")
    void testEmailValidation_Valid() {
        booking.setEmail("test@example.com");
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        booking.setStatus(1);
        booking.setIsDeleted(0);
        assertEquals(1, booking.getStatus());
        assertEquals(0, booking.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        booking.setCreatedAt(now);
        booking.setUpdatedAt(later);

        assertEquals(now, booking.getCreatedAt());
        assertEquals(later, booking.getUpdatedAt());
    }
}

