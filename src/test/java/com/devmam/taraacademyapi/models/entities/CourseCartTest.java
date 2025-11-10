package com.devmam.taraacademyapi.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CourseCart Entity Tests")
class CourseCartTest {

    private CourseCart courseCart;

    @BeforeEach
    void setUp() {
        courseCart = new CourseCart();
    }

    @Test
    @DisplayName("Test CourseCart NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(courseCart);
        assertNull(courseCart.getId());
    }

    @Test
    @DisplayName("Test CourseCart AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        CourseCart courseCart = new CourseCart(id, null, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, courseCart.getId());
        assertEquals(createdAt, courseCart.getCreatedAt());
        assertEquals(updatedAt, courseCart.getUpdatedAt());
        assertEquals(status, courseCart.getStatus());
        assertEquals(isDeleted, courseCart.getIsDeleted());
    }

    @Test
    @DisplayName("Test CourseCart Builder")
    void testBuilder() {
        CourseCart courseCart = CourseCart.builder()
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(1, courseCart.getStatus());
        assertEquals(0, courseCart.getIsDeleted());
    }

    @Test
    @DisplayName("Test CourseCart Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        courseCart.setId(id);
        courseCart.setStatus(1);
        courseCart.setIsDeleted(0);

        assertEquals(id, courseCart.getId());
        assertEquals(1, courseCart.getStatus());
        assertEquals(0, courseCart.getIsDeleted());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        courseCart.setStatus(1);
        courseCart.setIsDeleted(0);
        assertEquals(1, courseCart.getStatus());
        assertEquals(0, courseCart.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        courseCart.setCreatedAt(now);
        courseCart.setUpdatedAt(later);

        assertEquals(now, courseCart.getCreatedAt());
        assertEquals(later, courseCart.getUpdatedAt());
    }
}

