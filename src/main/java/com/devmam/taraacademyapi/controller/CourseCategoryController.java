package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.CourseCategoryRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CourseCategoryResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.CourseCategory;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CourseCategoryService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/course-categories")
@PreAuthorize("permitAll()")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Create a new course category
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CourseCategoryResponseDto>> createCourseCategory(@Valid @RequestBody CourseCategoryRequestDto request) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        CourseCategory courseCategory = CourseCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(currentUser)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .isDeleted(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        CourseCategory createdCourseCategory = courseCategoryService.create(courseCategory);
        CourseCategoryResponseDto responseDto = CourseCategoryResponseDto.toDTO(createdCourseCategory);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<CourseCategoryResponseDto>builder()
                        .status(201)
                        .message("Course category created successfully")
                        .error(null)
                        .data(responseDto)
                        .build());
    }

    /**
     * Get course category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<CourseCategoryResponseDto>> getCourseCategoryById(@PathVariable Integer id) {
        Optional<CourseCategory> courseCategory = courseCategoryService.getOne(id);
        if (courseCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CourseCategoryResponseDto>builder()
                            .status(404)
                            .message("Course category not found")
                            .error("Course category with id " + id + " not found")
                            .data(null)
                            .build());
        }

        CourseCategoryResponseDto responseDto = CourseCategoryResponseDto.toDTO(courseCategory.get());
        return ResponseEntity.ok(ResponseData.<CourseCategoryResponseDto>builder()
                .status(200)
                .message("Course category found")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Update course category by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CourseCategoryResponseDto>> updateCourseCategory(
            @PathVariable Integer id, 
            @Valid @RequestBody CourseCategoryRequestDto request) {
        
        Optional<CourseCategory> existingCourseCategory = courseCategoryService.getOne(id);
        if (existingCourseCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CourseCategoryResponseDto>builder()
                            .status(404)
                            .message("Course category not found")
                            .error("Course category with id " + id + " not found")
                            .data(null)
                            .build());
        }

        CourseCategory courseCategory = existingCourseCategory.get();
        courseCategory.setName(request.getName());
        courseCategory.setDescription(request.getDescription());
        courseCategory.setStatus(request.getStatus() != null ? request.getStatus() : courseCategory.getStatus());
        courseCategory.setUpdatedAt(Instant.now());

        CourseCategory updatedCourseCategory = courseCategoryService.update(id, courseCategory);
        CourseCategoryResponseDto responseDto = CourseCategoryResponseDto.toDTO(updatedCourseCategory);

        return ResponseEntity.ok(ResponseData.<CourseCategoryResponseDto>builder()
                .status(200)
                .message("Course category updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Delete course category by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteCourseCategory(@PathVariable Integer id) {
        Optional<CourseCategory> courseCategory = courseCategoryService.getOne(id);
        if (courseCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Void>builder()
                            .status(404)
                            .message("Course category not found")
                            .error("Course category with id " + id + " not found")
                            .data(null)
                            .build());
        }

        courseCategoryService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Course category deleted successfully")
                .error(null)
                .data(null)
                .build());
    }

    /**
     * Change course category status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CourseCategoryResponseDto>> changeCourseCategoryStatus(
            @PathVariable Integer id, 
            @RequestParam Integer status) {
        
        Optional<CourseCategory> courseCategory = courseCategoryService.getOne(id);
        if (courseCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CourseCategoryResponseDto>builder()
                            .status(404)
                            .message("Course category not found")
                            .error("Course category with id " + id + " not found")
                            .data(null)
                            .build());
        }

        CourseCategory updatedCourseCategory = courseCategoryService.changeStatus(id, status);
        CourseCategoryResponseDto responseDto = CourseCategoryResponseDto.toDTO(updatedCourseCategory);

        return ResponseEntity.ok(ResponseData.<CourseCategoryResponseDto>builder()
                .status(200)
                .message("Course category status updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Get all course categories
     */
    @GetMapping
    public ResponseEntity<ResponseData<List<CourseCategoryResponseDto>>> getAllCourseCategories() {
        List<CourseCategory> courseCategories = courseCategoryService.getAll();
        List<CourseCategoryResponseDto> responseDtos = courseCategories.stream()
                .map(CourseCategoryResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<CourseCategoryResponseDto>>builder()
                .status(200)
                .message("Course categories retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Filter course categories with pagination and sorting
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<CourseCategoryResponseDto>>> filterCourseCategories(@RequestBody BaseFilterRequest filter) {
        Page<CourseCategory> result = courseCategoryService.filter(filter);
        Page<CourseCategoryResponseDto> responsePage = CourseCategoryResponseDto.convertPage(result);

        return ResponseEntity.ok(ResponseData.<Page<CourseCategoryResponseDto>>builder()
                .status(200)
                .message("Course categories filtered successfully")
                .error(null)
                .data(responsePage)
                .build());
    }

    /**
     * Get course categories count
     */
    @GetMapping("/count")
    public ResponseEntity<ResponseData<Long>> getCourseCategoriesCount() {
        long count = courseCategoryService.count();
        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(200)
                .message("Course categories count retrieved successfully")
                .error(null)
                .data(count)
                .build());
    }
}
