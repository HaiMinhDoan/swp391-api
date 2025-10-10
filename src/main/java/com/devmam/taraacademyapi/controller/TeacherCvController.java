package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.TeacherCvRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.TeacherCvResponseDto;
import com.devmam.taraacademyapi.models.entities.TeacherCv;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.TeacherCvService;
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
@RequestMapping("/api/v1/teacher-cvs")
@PreAuthorize("permitAll()")
public class TeacherCvController {

    @Autowired
    private TeacherCvService teacherCvService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Create a new teacher CV
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<TeacherCvResponseDto>> createTeacherCv(@Valid @RequestBody TeacherCvRequestDto request) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.getOne(UUID.fromString(currentUserEmail)).orElse(null);
        }

        // Get user entity
        User user = userService.getOne(request.getUserId()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<TeacherCvResponseDto>builder()
                            .status(400)
                            .message("User not found")
                            .error("User with id " + request.getUserId() + " not found")
                            .data(null)
                            .build());
        }

        TeacherCv teacherCv = TeacherCv.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .cvUrl(request.getCvUrl())
                .experience(request.getExperience())
                .skills(request.getSkills())
                .educations(request.getEducations())
                .certificates(request.getCertificates())
                .createdBy(currentUser)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .isDeleted(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        TeacherCv createdTeacherCv = teacherCvService.create(teacherCv);
        TeacherCvResponseDto responseDto = TeacherCvResponseDto.toDTO(createdTeacherCv);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<TeacherCvResponseDto>builder()
                        .status(201)
                        .message("Teacher CV created successfully")
                        .error(null)
                        .data(responseDto)
                        .build());
    }

    /**
     * Get teacher CV by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<TeacherCvResponseDto>> getTeacherCvById(@PathVariable Integer id) {
        Optional<TeacherCv> teacherCv = teacherCvService.getOne(id);
        if (teacherCv.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<TeacherCvResponseDto>builder()
                            .status(404)
                            .message("Teacher CV not found")
                            .error("Teacher CV with id " + id + " not found")
                            .data(null)
                            .build());
        }

        TeacherCvResponseDto responseDto = TeacherCvResponseDto.toDTO(teacherCv.get());
        return ResponseEntity.ok(ResponseData.<TeacherCvResponseDto>builder()
                .status(200)
                .message("Teacher CV found")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Update teacher CV by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<TeacherCvResponseDto>> updateTeacherCv(
            @PathVariable Integer id, 
            @Valid @RequestBody TeacherCvRequestDto request) {
        
        Optional<TeacherCv> existingTeacherCv = teacherCvService.getOne(id);
        if (existingTeacherCv.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<TeacherCvResponseDto>builder()
                            .status(404)
                            .message("Teacher CV not found")
                            .error("Teacher CV with id " + id + " not found")
                            .data(null)
                            .build());
        }

        // Get user entity
        User user = userService.getOne(request.getUserId()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<TeacherCvResponseDto>builder()
                            .status(400)
                            .message("User not found")
                            .error("User with id " + request.getUserId() + " not found")
                            .data(null)
                            .build());
        }

        TeacherCv teacherCv = existingTeacherCv.get();
        teacherCv.setUser(user);
        teacherCv.setTitle(request.getTitle());
        teacherCv.setDescription(request.getDescription());
        teacherCv.setCvUrl(request.getCvUrl());
        teacherCv.setExperience(request.getExperience());
        teacherCv.setSkills(request.getSkills());
        teacherCv.setEducations(request.getEducations());
        teacherCv.setCertificates(request.getCertificates());
        teacherCv.setStatus(request.getStatus() != null ? request.getStatus() : teacherCv.getStatus());
        teacherCv.setUpdatedAt(Instant.now());

        TeacherCv updatedTeacherCv = teacherCvService.update(id, teacherCv);
        TeacherCvResponseDto responseDto = TeacherCvResponseDto.toDTO(updatedTeacherCv);

        return ResponseEntity.ok(ResponseData.<TeacherCvResponseDto>builder()
                .status(200)
                .message("Teacher CV updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Delete teacher CV by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteTeacherCv(@PathVariable Integer id) {
        Optional<TeacherCv> teacherCv = teacherCvService.getOne(id);
        if (teacherCv.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Void>builder()
                            .status(404)
                            .message("Teacher CV not found")
                            .error("Teacher CV with id " + id + " not found")
                            .data(null)
                            .build());
        }

        teacherCvService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Teacher CV deleted successfully")
                .error(null)
                .data(null)
                .build());
    }

    /**
     * Change teacher CV status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<TeacherCvResponseDto>> changeTeacherCvStatus(
            @PathVariable Integer id, 
            @RequestParam Integer status) {
        
        Optional<TeacherCv> teacherCv = teacherCvService.getOne(id);
        if (teacherCv.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<TeacherCvResponseDto>builder()
                            .status(404)
                            .message("Teacher CV not found")
                            .error("Teacher CV with id " + id + " not found")
                            .data(null)
                            .build());
        }

        TeacherCv updatedTeacherCv = teacherCvService.changeStatus(id, status);
        TeacherCvResponseDto responseDto = TeacherCvResponseDto.toDTO(updatedTeacherCv);

        return ResponseEntity.ok(ResponseData.<TeacherCvResponseDto>builder()
                .status(200)
                .message("Teacher CV status updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Get all teacher CVs
     */
    @GetMapping
    public ResponseEntity<ResponseData<List<TeacherCvResponseDto>>> getAllTeacherCvs() {
        List<TeacherCv> teacherCvs = teacherCvService.getAll();
        List<TeacherCvResponseDto> responseDtos = teacherCvs.stream()
                .map(TeacherCvResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<TeacherCvResponseDto>>builder()
                .status(200)
                .message("Teacher CVs retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Filter teacher CVs with pagination and sorting
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<TeacherCvResponseDto>>> filterTeacherCvs(@RequestBody BaseFilterRequest filter) {
        Page<TeacherCv> result = teacherCvService.filter(filter);
        Page<TeacherCvResponseDto> responsePage = TeacherCvResponseDto.convertPage(result);

        return ResponseEntity.ok(ResponseData.<Page<TeacherCvResponseDto>>builder()
                .status(200)
                .message("Teacher CVs filtered successfully")
                .error(null)
                .data(responsePage)
                .build());
    }

    /**
     * Get teacher CVs count
     */
    @GetMapping("/count")
    public ResponseEntity<ResponseData<Long>> getTeacherCvsCount() {
        long count = teacherCvService.count();
        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(200)
                .message("Teacher CVs count retrieved successfully")
                .error(null)
                .data(count)
                .build());
    }

    /**
     * Get teacher CV by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseData<TeacherCvResponseDto>> getTeacherCvByUserId(@PathVariable UUID userId) {
        List<TeacherCv> teacherCvs = teacherCvService.getAll();
        Optional<TeacherCv> userTeacherCv = teacherCvs.stream()
                .filter(cv -> cv.getUser() != null && cv.getUser().getId().equals(userId))
                .findFirst();

        if (userTeacherCv.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<TeacherCvResponseDto>builder()
                            .status(404)
                            .message("Teacher CV not found for user")
                            .error("Teacher CV with user id " + userId + " not found")
                            .data(null)
                            .build());
        }

        TeacherCvResponseDto responseDto = TeacherCvResponseDto.toDTO(userTeacherCv.get());
        return ResponseEntity.ok(ResponseData.<TeacherCvResponseDto>builder()
                .status(200)
                .message("Teacher CV found")
                .error(null)
                .data(responseDto)
                .build());
    }
}
