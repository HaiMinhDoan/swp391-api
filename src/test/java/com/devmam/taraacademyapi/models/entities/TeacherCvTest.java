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

@DisplayName("TeacherCv Entity Tests")
class TeacherCvTest {

    private Validator validator;
    private TeacherCv teacherCv;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        teacherCv = new TeacherCv();
    }

    @Test
    @DisplayName("Test TeacherCv NoArgsConstructor")
    void testNoArgsConstructor() {
        assertNotNull(teacherCv);
        assertNull(teacherCv.getId());
        assertNull(teacherCv.getTitle());
    }

    @Test
    @DisplayName("Test TeacherCv AllArgsConstructor")
    void testAllArgsConstructor() {
        Integer id = 1;
        String title = "Senior Teacher";
        String description = "Experienced teacher";
        String cvUrl = "https://example.com/cv.pdf";
        String experience = "10 years";
        String skills = "Java, Python";
        String educations = "Master's Degree";
        String certificates = "Oracle Certified";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Integer status = 1;
        Integer isDeleted = 0;

        TeacherCv teacherCv = new TeacherCv(id, null, title, description, cvUrl, experience, skills, educations, certificates, null, createdAt, updatedAt, status, isDeleted);

        assertEquals(id, teacherCv.getId());
        assertEquals(title, teacherCv.getTitle());
        assertEquals(description, teacherCv.getDescription());
        assertEquals(cvUrl, teacherCv.getCvUrl());
        assertEquals(experience, teacherCv.getExperience());
        assertEquals(skills, teacherCv.getSkills());
        assertEquals(educations, teacherCv.getEducations());
        assertEquals(certificates, teacherCv.getCertificates());
    }

    @Test
    @DisplayName("Test TeacherCv Builder")
    void testBuilder() {
        String title = "Junior Teacher";
        String cvUrl = "https://example.com/cv.pdf";

        TeacherCv teacherCv = TeacherCv.builder()
                .title(title)
                .cvUrl(cvUrl)
                .status(1)
                .isDeleted(0)
                .build();

        assertEquals(title, teacherCv.getTitle());
        assertEquals(cvUrl, teacherCv.getCvUrl());
    }

    @Test
    @DisplayName("Test TeacherCv Getters and Setters")
    void testGettersAndSetters() {
        Integer id = 1;
        String title = "Teacher";
        String description = "Description";
        String cvUrl = "cv.pdf";
        String experience = "5 years";
        String skills = "Java";
        String educations = "Bachelor";
        String certificates = "Certified";

        teacherCv.setId(id);
        teacherCv.setTitle(title);
        teacherCv.setDescription(description);
        teacherCv.setCvUrl(cvUrl);
        teacherCv.setExperience(experience);
        teacherCv.setSkills(skills);
        teacherCv.setEducations(educations);
        teacherCv.setCertificates(certificates);

        assertEquals(id, teacherCv.getId());
        assertEquals(title, teacherCv.getTitle());
        assertEquals(description, teacherCv.getDescription());
        assertEquals(cvUrl, teacherCv.getCvUrl());
        assertEquals(experience, teacherCv.getExperience());
        assertEquals(skills, teacherCv.getSkills());
        assertEquals(educations, teacherCv.getEducations());
        assertEquals(certificates, teacherCv.getCertificates());
    }

    @Test
    @DisplayName("Test Title validation - should accept valid title")
    void testTitleValidation_Valid() {
        teacherCv.setTitle("Valid Title");
        Set<ConstraintViolation<TeacherCv>> violations = validator.validate(teacherCv);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Title validation - should reject title exceeding max length")
    void testTitleValidation_ExceedsMaxLength() {
        String longTitle = "a".repeat(21); // 21 characters, max is 20
        teacherCv.setTitle(longTitle);
        Set<ConstraintViolation<TeacherCv>> violations = validator.validate(teacherCv);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Test Description - should handle long descriptions")
    void testDescription() {
        String longDescription = "a".repeat(1000);
        teacherCv.setDescription(longDescription);
        assertEquals(longDescription, teacherCv.getDescription());
    }

    @Test
    @DisplayName("Test CvUrl - should handle CV URLs")
    void testCvUrl() {
        String cvUrl = "https://example.com/cv.pdf";
        teacherCv.setCvUrl(cvUrl);
        assertEquals(cvUrl, teacherCv.getCvUrl());
    }

    @Test
    @DisplayName("Test Experience, Skills, Educations, Certificates - should handle long text")
    void testLongTextFields() {
        String experience = "a".repeat(1000);
        String skills = "b".repeat(2000);
        String educations = "c".repeat(1500);
        String certificates = "d".repeat(1200);

        teacherCv.setExperience(experience);
        teacherCv.setSkills(skills);
        teacherCv.setEducations(educations);
        teacherCv.setCertificates(certificates);

        assertEquals(experience, teacherCv.getExperience());
        assertEquals(skills, teacherCv.getSkills());
        assertEquals(educations, teacherCv.getEducations());
        assertEquals(certificates, teacherCv.getCertificates());
    }

    @Test
    @DisplayName("Test Status and IsDeleted")
    void testStatusAndIsDeleted() {
        teacherCv.setStatus(1);
        teacherCv.setIsDeleted(0);
        assertEquals(1, teacherCv.getStatus());
        assertEquals(0, teacherCv.getIsDeleted());
    }

    @Test
    @DisplayName("Test Timestamps")
    void testTimestamps() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        teacherCv.setCreatedAt(now);
        teacherCv.setUpdatedAt(later);

        assertEquals(now, teacherCv.getCreatedAt());
        assertEquals(later, teacherCv.getUpdatedAt());
    }
}

