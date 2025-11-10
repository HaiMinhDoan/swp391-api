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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tran Entity Tests")
class TranTest {

    private Validator validator;
    private Tran tran;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        tran = new Tran();
    }

    @Test
    @DisplayName("Test Tran NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(tran);
        assertNull(tran.getId());
        assertNull(tran.getAmount());
    }

    @Test
    @DisplayName("Test Tran AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        BigDecimal amount = new BigDecimal("1000.00");
        String method = "CREDIT_CARD";
        Map<String, Object> detail = new HashMap<>();
        detail.put("card", "1234");
        Integer responseCode = 200;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Tran tran = new Tran(id, null, amount, method, detail, responseCode, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, tran.getId());
        assertEquals(amount, tran.getAmount());
        assertEquals(method, tran.getMethod());
        assertEquals(detail, tran.getDetail());
        assertEquals(responseCode, tran.getResponseCode());
    }

    @Test
    @DisplayName("Test Tran Builder")
    void testBuilder() {
        BigDecimal amount = new BigDecimal("500.00");
        String method = "PAYPAL";

        Tran tran = Tran.builder()
                .amount(amount)
                .method(method)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(amount, tran.getAmount());
        assertEquals(method, tran.getMethod());
    }

    @Test
    @DisplayName("Test Tran Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        BigDecimal amount = new BigDecimal("750.50");
        String method = "BANK_TRANSFER";
        Map<String, Object> detail = new HashMap<>();
        detail.put("account", "123456");
        Integer responseCode = 201;

        tran.setId(id);
        tran.setAmount(amount);
        tran.setMethod(method);
        tran.setDetail(detail);
        tran.setResponseCode(responseCode);

        assertEquals(id, tran.getId());
        assertEquals(amount, tran.getAmount());
        assertEquals(method, tran.getMethod());
        assertEquals(detail, tran.getDetail());
        assertEquals(responseCode, tran.getResponseCode());
    }

    @Test
    @DisplayName("Test Method validation - should accept valid method")
    void testMethodValidation_Valid() {
        tran.setMethod("CREDIT_CARD");
        Set<ConstraintViolation<Tran>> violations = validator.validate(tran);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Method validation - should reject method exceeding max length")
    void testMethodValidation_ExceedsMaxLength() {
        String longMethod = "a".repeat(51); // 51 characters, max is 50
        tran.setMethod(longMethod);
        Set<ConstraintViolation<Tran>> violations = validator.validate(tran);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Amount - should handle BigDecimal values")
    void testAmount() {
        BigDecimal amount1 = new BigDecimal("0.00");
        BigDecimal amount2 = new BigDecimal("9999.99");
        BigDecimal amount3 = new BigDecimal("1234.56");

        tran.setAmount(amount1);
        assertEquals(amount1, tran.getAmount());

        tran.setAmount(amount2);
        assertEquals(amount2, tran.getAmount());

        tran.setAmount(amount3);
        assertEquals(amount3, tran.getAmount());
    }

    @Test
    @DisplayName("Test Detail - should handle Map values")
    void testDetail() {
        Map<String, Object> detail1 = new HashMap<>();
        detail1.put("key1", "value1");
        detail1.put("key2", 123);

        tran.setDetail(detail1);
        assertEquals(detail1, tran.getDetail());
        assertEquals("value1", tran.getDetail().get("key1"));
        assertEquals(123, tran.getDetail().get("key2"));

        Map<String, Object> detail2 = new HashMap<>();
        detail2.put("transaction_id", "TXN123");
        tran.setDetail(detail2);
        assertEquals(detail2, tran.getDetail());
    }

    @Test
    @DisplayName("Test ResponseCode - should handle response codes")
    void testResponseCode() {
        tran.setResponseCode(200);
        assertEquals(200, tran.getResponseCode());

        tran.setResponseCode(404);
        assertEquals(404, tran.getResponseCode());

        tran.setResponseCode(null);
        assertNull(tran.getResponseCode());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        tran.setStatus(1);
        tran.setIsDeleted(0);
        assertEquals(1, tran.getStatus());
        assertEquals(0, tran.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        tran.setCreatedAt(now);
        tran.setUpdatedAt(later);

        assertEquals(now, tran.getCreatedAt());
        assertEquals(later, tran.getUpdatedAt());
    }
}

