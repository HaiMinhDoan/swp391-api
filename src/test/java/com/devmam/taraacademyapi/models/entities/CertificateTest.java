package com.devmam.taraacademyapi.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Certificate Entity Tests")
class CertificateTest {

    private Certificate certificate;

    @BeforeEach
    void setUp() {
        certificate = new Certificate();
    }

    @Test
    @DisplayName("Test Certificate NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(certificate);
        assertNull(certificate.getId());
        assertNull(certificate.getImgUrl());
    }

    @Test
    @DisplayName("Test Certificate AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String imgUrl = "https://example.com/certificate.jpg";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        Certificate certificate = new Certificate(id, null, null, imgUrl, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, certificate.getId());
        assertEquals(imgUrl, certificate.getImgUrl());
        assertEquals(createdAt, certificate.getCreatedAt());
        assertEquals(updatedAt, certificate.getUpdatedAt());
        assertEquals(status, certificate.getStatus());
        assertEquals(isDeleted, certificate.getIsDeleted());
    }

    @Test
    @DisplayName("Test Certificate Builder")
    void testBuilder() {
        String imgUrl = "https://example.com/cert.png";

        Certificate certificate = Certificate.builder()
                .imgUrl(imgUrl)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(imgUrl, certificate.getImgUrl());
        assertEquals(1, certificate.getStatus());
        assertEquals(0, certificate.getIsDeleted());
    }

    @Test
    @DisplayName("Test Certificate Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String imgUrl = "https://example.com/certificate.png";

        certificate.setId(id);
        certificate.setImgUrl(imgUrl);
        certificate.setStatus(1);
        certificate.setIsDeleted(0);

        assertEquals(id, certificate.getId());
        assertEquals(imgUrl, certificate.getImgUrl());
        assertEquals(1, certificate.getStatus());
        assertEquals(0, certificate.getIsDeleted());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        certificate.setStatus(1);
        certificate.setIsDeleted(0);
        assertEquals(1, certificate.getStatus());
        assertEquals(0, certificate.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        certificate.setCreatedAt(now);
        certificate.setUpdatedAt(later);

        assertEquals(now, certificate.getCreatedAt());
        assertEquals(later, certificate.getUpdatedAt());
    }
}

