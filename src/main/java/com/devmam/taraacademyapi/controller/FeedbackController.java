package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.FeedbackRequestDto;
import com.devmam.taraacademyapi.models.dto.response.FeedbackResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Feedback;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.FeedbackService;
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
@RequestMapping("/api/v1/feedbacks")
@PreAuthorize("permitAll()")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    /**
     * Create a new feedback
     */
    @PostMapping
    public ResponseEntity<ResponseData<FeedbackResponseDto>> createFeedback(@Valid @RequestBody FeedbackRequestDto request) {
        // Get user entity
        User user = userService.getOne(request.getUserId()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<FeedbackResponseDto>builder()
                            .status(400)
                            .message("User not found")
                            .error("User with id " + request.getUserId() + " not found")
                            .data(null)
                            .build());
        }

        Feedback feedback = Feedback.builder()
                .user(user)
                .referenceType(request.getReferenceType())
                .referenceId(request.getReferenceId())
                .rating(request.getRating())
                .comment(request.getComment())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .isDeleted(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Feedback createdFeedback = feedbackService.create(feedback);
        FeedbackResponseDto responseDto = FeedbackResponseDto.toDTO(createdFeedback);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<FeedbackResponseDto>builder()
                        .status(201)
                        .message("Feedback created successfully")
                        .error(null)
                        .data(responseDto)
                        .build());
    }

    /**
     * Get feedback by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<FeedbackResponseDto>> getFeedbackById(@PathVariable Integer id) {
        Optional<Feedback> feedback = feedbackService.getOne(id);
        if (feedback.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<FeedbackResponseDto>builder()
                            .status(404)
                            .message("Feedback not found")
                            .error("Feedback with id " + id + " not found")
                            .data(null)
                            .build());
        }

        FeedbackResponseDto responseDto = FeedbackResponseDto.toDTO(feedback.get());
        return ResponseEntity.ok(ResponseData.<FeedbackResponseDto>builder()
                .status(200)
                .message("Feedback found")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Update feedback by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<FeedbackResponseDto>> updateFeedback(
            @PathVariable Integer id, 
            @Valid @RequestBody FeedbackRequestDto request) {
        
        Optional<Feedback> existingFeedback = feedbackService.getOne(id);
        if (existingFeedback.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<FeedbackResponseDto>builder()
                            .status(404)
                            .message("Feedback not found")
                            .error("Feedback with id " + id + " not found")
                            .data(null)
                            .build());
        }

        // Get user entity
        User user = userService.getOne(request.getUserId()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<FeedbackResponseDto>builder()
                            .status(400)
                            .message("User not found")
                            .error("User with id " + request.getUserId() + " not found")
                            .data(null)
                            .build());
        }

        Feedback feedback = existingFeedback.get();
        feedback.setUser(user);
        feedback.setReferenceType(request.getReferenceType());
        feedback.setReferenceId(request.getReferenceId());
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setStatus(request.getStatus() != null ? request.getStatus() : feedback.getStatus());
        feedback.setUpdatedAt(Instant.now());

        Feedback updatedFeedback = feedbackService.update(id, feedback);
        FeedbackResponseDto responseDto = FeedbackResponseDto.toDTO(updatedFeedback);

        return ResponseEntity.ok(ResponseData.<FeedbackResponseDto>builder()
                .status(200)
                .message("Feedback updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Delete feedback by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteFeedback(@PathVariable Integer id) {
        Optional<Feedback> feedback = feedbackService.getOne(id);
        if (feedback.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Void>builder()
                            .status(404)
                            .message("Feedback not found")
                            .error("Feedback with id " + id + " not found")
                            .data(null)
                            .build());
        }

        feedbackService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Feedback deleted successfully")
                .error(null)
                .data(null)
                .build());
    }

    /**
     * Change feedback status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<FeedbackResponseDto>> changeFeedbackStatus(
            @PathVariable Integer id, 
            @RequestParam Integer status) {
        
        Optional<Feedback> feedback = feedbackService.getOne(id);
        if (feedback.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<FeedbackResponseDto>builder()
                            .status(404)
                            .message("Feedback not found")
                            .error("Feedback with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Feedback updatedFeedback = feedbackService.changeStatus(id, status);
        FeedbackResponseDto responseDto = FeedbackResponseDto.toDTO(updatedFeedback);

        return ResponseEntity.ok(ResponseData.<FeedbackResponseDto>builder()
                .status(200)
                .message("Feedback status updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Get all feedbacks
     */
    @GetMapping
    public ResponseEntity<ResponseData<List<FeedbackResponseDto>>> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackService.getAll();
        List<FeedbackResponseDto> responseDtos = feedbacks.stream()
                .map(FeedbackResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<FeedbackResponseDto>>builder()
                .status(200)
                .message("Feedbacks retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Filter feedbacks with pagination and sorting
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<FeedbackResponseDto>>> filterFeedbacks(@RequestBody BaseFilterRequest filter) {
        Page<Feedback> result = feedbackService.filter(filter);
        Page<FeedbackResponseDto> responsePage = FeedbackResponseDto.convertPage(result);

        return ResponseEntity.ok(ResponseData.<Page<FeedbackResponseDto>>builder()
                .status(200)
                .message("Feedbacks filtered successfully")
                .error(null)
                .data(responsePage)
                .build());
    }

    /**
     * Get feedbacks count
     */
    @GetMapping("/count")
    public ResponseEntity<ResponseData<Long>> getFeedbacksCount() {
        long count = feedbackService.count();
        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(200)
                .message("Feedbacks count retrieved successfully")
                .error(null)
                .data(count)
                .build());
    }

    /**
     * Get feedbacks by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseData<List<FeedbackResponseDto>>> getFeedbacksByUserId(@PathVariable UUID userId) {
        List<Feedback> feedbacks = feedbackService.getAll();
        List<FeedbackResponseDto> userFeedbacks = feedbacks.stream()
                .filter(feedback -> feedback.getUser() != null && feedback.getUser().getId().equals(userId))
                .map(FeedbackResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<FeedbackResponseDto>>builder()
                .status(200)
                .message("User feedbacks retrieved successfully")
                .error(null)
                .data(userFeedbacks)
                .build());
    }

    /**
     * Get feedbacks by reference type and ID
     */
    @GetMapping("/reference/{referenceType}/{referenceId}")
    public ResponseEntity<ResponseData<List<FeedbackResponseDto>>> getFeedbacksByReference(
            @PathVariable String referenceType, 
            @PathVariable Integer referenceId) {
        List<Feedback> feedbacks = feedbackService.getAll();
        List<FeedbackResponseDto> referenceFeedbacks = feedbacks.stream()
                .filter(feedback -> referenceType.equals(feedback.getReferenceType()) && 
                                  referenceId.equals(feedback.getReferenceId()))
                .map(FeedbackResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<FeedbackResponseDto>>builder()
                .status(200)
                .message("Reference feedbacks retrieved successfully")
                .error(null)
                .data(referenceFeedbacks)
                .build());
    }
}
