package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.CareerRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CareerResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Career;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CareerService;
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
@RequestMapping("/api/v1/careers")
@PreAuthorize("permitAll()")
public class CareerController {

    @Autowired
    private CareerService careerService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Create a new career
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CareerResponseDto>> createCareer(@Valid @RequestBody CareerRequestDto request) {
        // Get current user
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.getOne(UUID.fromString(currentUserEmail)).orElse(null);
        }

        Career career = Career.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .isDeleted(0)
                .createdBy(currentUser)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Career createdCareer = careerService.create(career);
        CareerResponseDto responseDto = CareerResponseDto.toDTO(createdCareer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<CareerResponseDto>builder()
                        .status(201)
                        .message("Career created successfully")
                        .error(null)
                        .data(responseDto)
                        .build());
    }

    /**
     * Get career by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<CareerResponseDto>> getCareerById(@PathVariable Integer id) {
        Optional<Career> career = careerService.getOne(id);
        if (career.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CareerResponseDto>builder()
                            .status(404)
                            .message("Career not found")
                            .error("Career with id " + id + " not found")
                            .data(null)
                            .build());
        }

        CareerResponseDto responseDto = CareerResponseDto.toDTO(career.get());
        return ResponseEntity.ok(ResponseData.<CareerResponseDto>builder()
                .status(200)
                .message("Career found")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Update career by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CareerResponseDto>> updateCareer(
            @PathVariable Integer id, 
            @Valid @RequestBody CareerRequestDto request) {
        
        Optional<Career> existingCareer = careerService.getOne(id);
        if (existingCareer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CareerResponseDto>builder()
                            .status(404)
                            .message("Career not found")
                            .error("Career with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Career career = existingCareer.get();
        career.setTitle(request.getTitle());
        career.setDescription(request.getDescription());
        career.setStatus(request.getStatus() != null ? request.getStatus() : career.getStatus());
        career.setUpdatedAt(Instant.now());

        Career updatedCareer = careerService.update(id, career);
        CareerResponseDto responseDto = CareerResponseDto.toDTO(updatedCareer);

        return ResponseEntity.ok(ResponseData.<CareerResponseDto>builder()
                .status(200)
                .message("Career updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Delete career by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteCareer(@PathVariable Integer id) {
        Optional<Career> career = careerService.getOne(id);
        if (career.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Void>builder()
                            .status(404)
                            .message("Career not found")
                            .error("Career with id " + id + " not found")
                            .data(null)
                            .build());
        }

        careerService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Career deleted successfully")
                .error(null)
                .data(null)
                .build());
    }

    /**
     * Change career status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<CareerResponseDto>> changeCareerStatus(
            @PathVariable Integer id, 
            @RequestParam Integer status) {
        
        Optional<Career> career = careerService.getOne(id);
        if (career.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CareerResponseDto>builder()
                            .status(404)
                            .message("Career not found")
                            .error("Career with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Career updatedCareer = careerService.changeStatus(id, status);
        CareerResponseDto responseDto = CareerResponseDto.toDTO(updatedCareer);

        return ResponseEntity.ok(ResponseData.<CareerResponseDto>builder()
                .status(200)
                .message("Career status updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Get all careers
     */
    @GetMapping
    public ResponseEntity<ResponseData<List<CareerResponseDto>>> getAllCareers() {
        List<Career> careers = careerService.getAll();
        List<CareerResponseDto> responseDtos = careers.stream()
                .map(CareerResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<CareerResponseDto>>builder()
                .status(200)
                .message("Careers retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Filter careers with pagination and sorting
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<CareerResponseDto>>> filterCareers(@RequestBody BaseFilterRequest filter) {
        Page<Career> result = careerService.filter(filter);
        Page<CareerResponseDto> responsePage = CareerResponseDto.convertPage(result);

        return ResponseEntity.ok(ResponseData.<Page<CareerResponseDto>>builder()
                .status(200)
                .message("Careers filtered successfully")
                .error(null)
                .data(responsePage)
                .build());
    }

    /**
     * Get careers count
     */
    @GetMapping("/count")
    public ResponseEntity<ResponseData<Long>> getCareersCount() {
        long count = careerService.count();
        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(200)
                .message("Careers count retrieved successfully")
                .error(null)
                .data(count)
                .build());
    }
}
