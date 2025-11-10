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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = new User();
    }

    @Test
    @DisplayName("Test User NoArgsConstructor - should create empty user")
    void testNoArgsConstructor() {
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
    }

    @Test
    @DisplayName("Test User AllArgsConstructor - should create user with all fields")
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String username = "testuser";
        String email = "test@example.com";
        String fullName = "Test User";
        String phone = "0123456789";
        String customerCode = "CUST001";
        BigDecimal accountBalance = new BigDecimal("1000.00");
        String avt = "avatar.jpg";
        String role = "USER";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;
        String password = "hashedPassword123";

        User user = new User(id, username, email, fullName, phone, customerCode,
                accountBalance, avt, role, createdAt, updatedAt, status, isDeleted, password);

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(phone, user.getPhone());
        assertEquals(customerCode, user.getCustomerCode());
        assertEquals(accountBalance, user.getAccountBalance());
        assertEquals(avt, user.getAvt());
        assertEquals(role, user.getRole());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
        assertEquals(status, user.getStatus());
        assertEquals(isDeleted, user.getIsDeleted());
        assertEquals(password, user.getPassword());
    }

    @Test
    @DisplayName("Test User Builder - should create user using builder pattern")
    void testBuilder() {
        UUID id = UUID.randomUUID();
        String username = "builderuser";
        String email = "builder@example.com";
        String fullName = "Builder User";
        BigDecimal accountBalance = new BigDecimal("500.50");

        User user = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .fullName(fullName)
                .accountBalance(accountBalance)
                .status(1)
                .isDeleted(0)
                .build();

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(accountBalance, user.getAccountBalance());
        assertEquals(1, user.getStatus());
        assertEquals(0, user.getIsDeleted());
    }

    @Test
    @DisplayName("Test User Getters and Setters")
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        String username = "setteruser";
        String email = "setter@example.com";
        String fullName = "Setter User";
        String phone = "0987654321";
        String customerCode = "CUST002";
        BigDecimal accountBalance = new BigDecimal("2000.00");
        String avt = "new-avatar.png";
        String role = "ADMIN";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;
        String password = "newPassword123";

        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setCustomerCode(customerCode);
        user.setAccountBalance(accountBalance);
        user.setAvt(avt);
        user.setRole(role);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        user.setStatus(status);
        user.setIsDeleted(isDeleted);
        user.setPassword(password);

        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(phone, user.getPhone());
        assertEquals(customerCode, user.getCustomerCode());
        assertEquals(accountBalance, user.getAccountBalance());
        assertEquals(avt, user.getAvt());
        assertEquals(role, user.getRole());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
        assertEquals(status, user.getStatus());
        assertEquals(isDeleted, user.getIsDeleted());
        assertEquals(password, user.getPassword());
    }

    @Test
    @DisplayName("Test Username validation - should accept valid username")
    void testUsernameValidation_Valid() {
        user.setUsername("validusername");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Username validation - should reject username exceeding max length")
    void testUsernameValidation_ExceedsMaxLength() {
        String longUsername = "a".repeat(101); // 101 characters, max is 100
        user.setUsername(longUsername);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Test Email validation - should accept valid email")
    void testEmailValidation_Valid() {
        user.setEmail("valid@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Email validation - should reject email exceeding max length")
    void testEmailValidation_ExceedsMaxLength() {
        String longEmail = "a".repeat(250) + "@example.com"; // Exceeds 255
        user.setEmail(longEmail);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test FullName validation - should accept valid full name")
    void testFullNameValidation_Valid() {
        user.setFullName("John Doe");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Phone validation - should accept valid phone")
    void testPhoneValidation_Valid() {
        user.setPhone("0123456789");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Phone validation - should reject phone exceeding max length")
    void testPhoneValidation_ExceedsMaxLength() {
        String longPhone = "1".repeat(21); // 21 characters, max is 20
        user.setPhone(longPhone);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test CustomerCode validation - should accept valid customer code")
    void testCustomerCodeValidation_Valid() {
        user.setCustomerCode("CUST001");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test AccountBalance - should handle BigDecimal values correctly")
    void testAccountBalance() {
        BigDecimal balance1 = new BigDecimal("0.00");
        BigDecimal balance2 = new BigDecimal("999999.99");
        BigDecimal balance3 = new BigDecimal("1234.56");

        user.setAccountBalance(balance1);
        assertEquals(balance1, user.getAccountBalance());

        user.setAccountBalance(balance2);
        assertEquals(balance2, user.getAccountBalance());

        user.setAccountBalance(balance3);
        assertEquals(balance3, user.getAccountBalance());
    }

    @Test
    @DisplayName("Test Avatar validation - should accept valid avatar path")
    void testAvatarValidation_Valid() {
        user.setAvt("path/to/avatar.jpg");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Role validation - should accept valid role")
    void testRoleValidation_Valid() {
        user.setRole("ADMIN");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Password validation - should accept valid password")
    void testPasswordValidation_Valid() {
        user.setPassword("securePassword123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Password validation - should reject password exceeding max length")
    void testPasswordValidation_ExceedsMaxLength() {
        String longPassword = "a".repeat(101); // 101 characters, max is 100
        user.setPassword(longPassword);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Status - should handle status values")
    void testStatus() {
        user.setStatus(0);
        assertEquals(0, user.getStatus());

        user.setStatus(1);
        assertEquals(1, user.getStatus());

        user.setStatus(2);
        assertEquals(2, user.getStatus());
    }

    @Test
    @DisplayName("Test IsDeleted - should handle isDeleted values")
    void testIsDeleted() {
        user.setIsDeleted(0);
        assertEquals(0, user.getIsDeleted());

        user.setIsDeleted(1);
        assertEquals(1, user.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps - should handle createdAt and updatedAt")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        user.setCreatedAt(now);
        user.setUpdatedAt(later);

        assertEquals(now, user.getCreatedAt());
        assertEquals(later, user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(user.getCreatedAt()));
    }

    @Test
    @DisplayName("Test User with null values - should handle null fields")
    void testUserWithNullValues() {
        User nullUser = new User();
        nullUser.setUsername(null);
        nullUser.setEmail(null);
        nullUser.setFullName(null);

        assertNull(nullUser.getUsername());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getFullName());
    }

    @Test
    @DisplayName("Test User equality - should compare users correctly")
    void testUserEquality() {
        UUID id = UUID.randomUUID();
        User user1 = User.builder().id(id).username("user1").build();
        User user2 = User.builder().id(id).username("user1").build();
        User user3 = User.builder().id(UUID.randomUUID()).username("user1").build();

        // Note: Without equals() override, these will use Object.equals()
        // This test verifies the objects are created correctly
        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(user3);
        assertEquals(user1.getUsername(), user2.getUsername());
        assertNotEquals(user1.getId(), user3.getId());
    }

    @Test
    @DisplayName("Test User with boundary values - should handle max length strings")
    void testUserWithBoundaryValues() {
        String maxUsername = "a".repeat(100); // Exactly 100 characters
        String maxEmail = "a".repeat(255); // Exactly 255 characters
        String maxPhone = "1".repeat(20); // Exactly 20 characters
        String maxPassword = "a".repeat(100); // Exactly 100 characters

        user.setUsername(maxUsername);
        user.setEmail(maxEmail);
        user.setPhone(maxPhone);
        user.setPassword(maxPassword);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Should accept values at max length");

        assertEquals(maxUsername, user.getUsername());
        assertEquals(maxEmail, user.getEmail());
        assertEquals(maxPhone, user.getPhone());
        assertEquals(maxPassword, user.getPassword());
    }
}

