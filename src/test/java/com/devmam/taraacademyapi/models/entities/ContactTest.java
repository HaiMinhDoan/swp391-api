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

@DisplayName("Contact Entity Tests")
class ContactTest {

    private Validator validator;
    private Contact contact;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        contact = new Contact();
    }

    @Test
    @DisplayName("Test Contact NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(contact);
        assertNull(contact.getId());
        assertNull(contact.getFullName());
    }

    @Test
    @DisplayName("Test Contact AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String fullName = "John Doe";
        String phone = "0123456789";
        String email = "john@example.com";
        String company = "Test Company";
        String personalRole = "Manager";
        String subject = "Inquiry";
        String message = "Test message";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Contact contact = new Contact(id, fullName, phone, email, company, personalRole, subject, message, createdAt, updatedAt, status, isDeleted, null);

        assertEquals(id, contact.getId());
        assertEquals(fullName, contact.getFullName());
        assertEquals(phone, contact.getPhone());
        assertEquals(email, contact.getEmail());
        assertEquals(company, contact.getCompany());
        assertEquals(personalRole, contact.getPersonalRole());
        assertEquals(subject, contact.getSubject());
        assertEquals(message, contact.getMessage());
    }

    @Test
    @DisplayName("Test Contact Builder")
    void testBuilder() {
        String fullName = "Jane Doe";
        String email = "jane@example.com";
        String subject = "Question";

        Contact contact = Contact.builder()
                .fullName(fullName)
                .email(email)
                .subject(subject)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(fullName, contact.getFullName());
        assertEquals(email, contact.getEmail());
        assertEquals(subject, contact.getSubject());
    }

    @Test
    @DisplayName("Test Contact Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String fullName = "Test User";
        String phone = "0987654321";
        String email = "test@example.com";
        String company = "Company";
        String personalRole = "Role";
        String subject = "Subject";
        String message = "Message";

        contact.setId(id);
        contact.setFullName(fullName);
        contact.setPhone(phone);
        contact.setEmail(email);
        contact.setCompany(company);
        contact.setPersonalRole(personalRole);
        contact.setSubject(subject);
        contact.setMessage(message);

        assertEquals(id, contact.getId());
        assertEquals(fullName, contact.getFullName());
        assertEquals(phone, contact.getPhone());
        assertEquals(email, contact.getEmail());
        assertEquals(company, contact.getCompany());
        assertEquals(personalRole, contact.getPersonalRole());
        assertEquals(subject, contact.getSubject());
        assertEquals(message, contact.getMessage());
    }

    @Test
    @DisplayName("Test FullName validation - should accept valid full name")
    void testFullNameValidation_Valid() {
        contact.setFullName("John Doe");
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test FullName validation - should reject full name exceeding max length")
    void testFullNameValidation_ExceedsMaxLength() {
        String longFullName = "a".repeat(256); // 256 characters, max is 255
        contact.setFullName(longFullName);
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Phone validation - should accept valid phone")
    void testPhoneValidation_Valid() {
        contact.setPhone("0123456789");
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Email validation - should accept valid email")
    void testEmailValidation_Valid() {
        contact.setEmail("test@example.com");
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        contact.setStatus(1);
        contact.setIsDeleted(0);
        assertEquals(1, contact.getStatus());
        assertEquals(0, contact.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        contact.setCreatedAt(now);
        contact.setUpdatedAt(later);

        assertEquals(now, contact.getCreatedAt());
        assertEquals(later, contact.getUpdatedAt());
    }
}

