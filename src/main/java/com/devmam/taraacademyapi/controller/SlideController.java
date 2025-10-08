package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.SlideRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.SlideResponseDto;
import com.devmam.taraacademyapi.models.entities.Slide;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.SlideService;
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
@RequestMapping("/api/v1/slides")
@PreAuthorize("permitAll()")
public class SlideController {

    @Autowired
    private SlideService slideService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Create a new slide
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<SlideResponseDto>> createSlide(@Valid @RequestBody SlideRequestDto request) {
        // Get current user
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        Slide slide = Slide.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .isDeleted(0)
                .createdBy(currentUser)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Slide createdSlide = slideService.create(slide);
        SlideResponseDto responseDto = SlideResponseDto.toDTO(createdSlide);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<SlideResponseDto>builder()
                        .status(201)
                        .message("Slide created successfully")
                        .error(null)
                        .data(responseDto)
                        .build());
    }

    /**
     * Get slide by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<SlideResponseDto>> getSlideById(@PathVariable Integer id) {
        Optional<Slide> slide = slideService.getOne(id);
        if (slide.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SlideResponseDto>builder()
                            .status(404)
                            .message("Slide not found")
                            .error("Slide with id " + id + " not found")
                            .data(null)
                            .build());
        }

        SlideResponseDto responseDto = SlideResponseDto.toDTO(slide.get());
        return ResponseEntity.ok(ResponseData.<SlideResponseDto>builder()
                .status(200)
                .message("Slide found")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Update slide by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<SlideResponseDto>> updateSlide(
            @PathVariable Integer id, 
            @Valid @RequestBody SlideRequestDto request) {
        
        Optional<Slide> existingSlide = slideService.getOne(id);
        if (existingSlide.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SlideResponseDto>builder()
                            .status(404)
                            .message("Slide not found")
                            .error("Slide with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Slide slide = existingSlide.get();
        slide.setTitle(request.getTitle());
        slide.setDescription(request.getDescription());
        slide.setImageUrl(request.getImageUrl());
        slide.setLinkUrl(request.getLinkUrl());
        slide.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : slide.getOrderIndex());
        slide.setStatus(request.getStatus() != null ? request.getStatus() : slide.getStatus());
        slide.setUpdatedAt(Instant.now());

        Slide updatedSlide = slideService.update(id, slide);
        SlideResponseDto responseDto = SlideResponseDto.toDTO(updatedSlide);

        return ResponseEntity.ok(ResponseData.<SlideResponseDto>builder()
                .status(200)
                .message("Slide updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Delete slide by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteSlide(@PathVariable Integer id) {
        Optional<Slide> slide = slideService.getOne(id);
        if (slide.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Void>builder()
                            .status(404)
                            .message("Slide not found")
                            .error("Slide with id " + id + " not found")
                            .data(null)
                            .build());
        }

        slideService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Slide deleted successfully")
                .error(null)
                .data(null)
                .build());
    }

    /**
     * Change slide status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<SlideResponseDto>> changeSlideStatus(
            @PathVariable Integer id, 
            @RequestParam Integer status) {
        
        Optional<Slide> slide = slideService.getOne(id);
        if (slide.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SlideResponseDto>builder()
                            .status(404)
                            .message("Slide not found")
                            .error("Slide with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Slide updatedSlide = slideService.changeStatus(id, status);
        SlideResponseDto responseDto = SlideResponseDto.toDTO(updatedSlide);

        return ResponseEntity.ok(ResponseData.<SlideResponseDto>builder()
                .status(200)
                .message("Slide status updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Get all slides
     */
    @GetMapping
    public ResponseEntity<ResponseData<List<SlideResponseDto>>> getAllSlides() {
        List<Slide> slides = slideService.getAll();
        List<SlideResponseDto> responseDtos = slides.stream()
                .map(SlideResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<SlideResponseDto>>builder()
                .status(200)
                .message("Slides retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Filter slides with pagination and sorting
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<SlideResponseDto>>> filterSlides(@RequestBody BaseFilterRequest filter) {
        Page<Slide> result = slideService.filter(filter);
        Page<SlideResponseDto> responsePage = SlideResponseDto.convertPage(result);

        return ResponseEntity.ok(ResponseData.<Page<SlideResponseDto>>builder()
                .status(200)
                .message("Slides filtered successfully")
                .error(null)
                .data(responsePage)
                .build());
    }

    /**
     * Get slides count
     */
    @GetMapping("/count")
    public ResponseEntity<ResponseData<Long>> getSlidesCount() {
        long count = slideService.count();
        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(200)
                .message("Slides count retrieved successfully")
                .error(null)
                .data(count)
                .build());
    }
}
