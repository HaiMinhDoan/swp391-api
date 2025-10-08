package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.CertificateRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CertificateResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Certificate;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CertificateService;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/certificates")
@PreAuthorize("permitAll()")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private JwtService jwtService;

    /**
     * Create a new certificate
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CertificateResponseDto>> createCertificate(@Valid @RequestBody CertificateRequestDto request) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        // Get user and course entities
        User user = userService.getOne(request.getUserId()).orElse(null);
        Course course = courseService.getOne(request.getCourseId()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<CertificateResponseDto>builder()
                            .status(400)
                            .message("User not found")
                            .error("User with id " + request.getUserId() + " not found")
                            .data(null)
                            .build());
        }

        if (course == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<CertificateResponseDto>builder()
                            .status(400)
                            .message("Course not found")
                            .error("Course with id " + request.getCourseId() + " not found")
                            .data(null)
                            .build());
        }

        Certificate certificate = Certificate.builder()
                .user(user)
                .course(course)
                .imgUrl(request.getImgUrl())
                .createdBy(currentUser)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .isDeleted(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Certificate createdCertificate = certificateService.create(certificate);
        CertificateResponseDto responseDto = CertificateResponseDto.toDTO(createdCertificate);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<CertificateResponseDto>builder()
                        .status(201)
                        .message("Certificate created successfully")
                        .error(null)
                        .data(responseDto)
                        .build());
    }

    /**
     * Get certificate by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<CertificateResponseDto>> getCertificateById(@PathVariable Integer id) {
        Optional<Certificate> certificate = certificateService.getOne(id);
        if (certificate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CertificateResponseDto>builder()
                            .status(404)
                            .message("Certificate not found")
                            .error("Certificate with id " + id + " not found")
                            .data(null)
                            .build());
        }

        CertificateResponseDto responseDto = CertificateResponseDto.toDTO(certificate.get());
        return ResponseEntity.ok(ResponseData.<CertificateResponseDto>builder()
                .status(200)
                .message("Certificate found")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Update certificate by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CertificateResponseDto>> updateCertificate(
            @PathVariable Integer id, 
            @Valid @RequestBody CertificateRequestDto request) {
        
        Optional<Certificate> existingCertificate = certificateService.getOne(id);
        if (existingCertificate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CertificateResponseDto>builder()
                            .status(404)
                            .message("Certificate not found")
                            .error("Certificate with id " + id + " not found")
                            .data(null)
                            .build());
        }

        // Get user and course entities
        User user = userService.getOne(request.getUserId()).orElse(null);
        Course course = courseService.getOne(request.getCourseId()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<CertificateResponseDto>builder()
                            .status(400)
                            .message("User not found")
                            .error("User with id " + request.getUserId() + " not found")
                            .data(null)
                            .build());
        }

        if (course == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<CertificateResponseDto>builder()
                            .status(400)
                            .message("Course not found")
                            .error("Course with id " + request.getCourseId() + " not found")
                            .data(null)
                            .build());
        }

        Certificate certificate = existingCertificate.get();
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setImgUrl(request.getImgUrl());
        certificate.setStatus(request.getStatus() != null ? request.getStatus() : certificate.getStatus());
        certificate.setUpdatedAt(Instant.now());

        Certificate updatedCertificate = certificateService.update(id, certificate);
        CertificateResponseDto responseDto = CertificateResponseDto.toDTO(updatedCertificate);

        return ResponseEntity.ok(ResponseData.<CertificateResponseDto>builder()
                .status(200)
                .message("Certificate updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Delete certificate by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteCertificate(@PathVariable Integer id) {
        Optional<Certificate> certificate = certificateService.getOne(id);
        if (certificate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Void>builder()
                            .status(404)
                            .message("Certificate not found")
                            .error("Certificate with id " + id + " not found")
                            .data(null)
                            .build());
        }

        certificateService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Certificate deleted successfully")
                .error(null)
                .data(null)
                .build());
    }

    /**
     * Change certificate status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CertificateResponseDto>> changeCertificateStatus(
            @PathVariable Integer id, 
            @RequestParam Integer status) {
        
        Optional<Certificate> certificate = certificateService.getOne(id);
        if (certificate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CertificateResponseDto>builder()
                            .status(404)
                            .message("Certificate not found")
                            .error("Certificate with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Certificate updatedCertificate = certificateService.changeStatus(id, status);
        CertificateResponseDto responseDto = CertificateResponseDto.toDTO(updatedCertificate);

        return ResponseEntity.ok(ResponseData.<CertificateResponseDto>builder()
                .status(200)
                .message("Certificate status updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Get all certificates
     */
    @GetMapping
    public ResponseEntity<ResponseData<List<CertificateResponseDto>>> getAllCertificates() {
        List<Certificate> certificates = certificateService.getAll();
        List<CertificateResponseDto> responseDtos = certificates.stream()
                .map(CertificateResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<CertificateResponseDto>>builder()
                .status(200)
                .message("Certificates retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Filter certificates with pagination and sorting
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<CertificateResponseDto>>> filterCertificates(@RequestBody BaseFilterRequest filter) {
        Page<Certificate> result = certificateService.filter(filter);
        Page<CertificateResponseDto> responsePage = CertificateResponseDto.convertPage(result);

        return ResponseEntity.ok(ResponseData.<Page<CertificateResponseDto>>builder()
                .status(200)
                .message("Certificates filtered successfully")
                .error(null)
                .data(responsePage)
                .build());
    }

    /**
     * Get certificates count
     */
    @GetMapping("/count")
    public ResponseEntity<ResponseData<Long>> getCertificatesCount() {
        long count = certificateService.count();
        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(200)
                .message("Certificates count retrieved successfully")
                .error(null)
                .data(count)
                .build());
    }

    /**
     * Get certificates by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseData<List<CertificateResponseDto>>> getCertificatesByUserId(@PathVariable UUID userId) {
        List<Certificate> certificates = certificateService.getAll();
        List<CertificateResponseDto> userCertificates = certificates.stream()
                .filter(cert -> cert.getUser() != null && cert.getUser().getId().equals(userId))
                .map(CertificateResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<CertificateResponseDto>>builder()
                .status(200)
                .message("User certificates retrieved successfully")
                .error(null)
                .data(userCertificates)
                .build());
    }
}
