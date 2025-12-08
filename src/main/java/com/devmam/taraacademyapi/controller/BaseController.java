package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.BaseService;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base Controller với các CRUD operations chung
 * @param <T> Entity type
 * @param <ID> ID type
 * @param <RequestDto> Request DTO type
 * @param <ResponseDto> Response DTO type
 */
public abstract class BaseController<T, ID, RequestDto, ResponseDto> {

    protected final BaseService<T, ID> baseService;

    @Autowired(required = false)
    protected JwtService jwtService;

    @Autowired(required = false)
    protected UserService userService;

    public BaseController(BaseService<T, ID> baseService) {
        this.baseService = baseService;
    }

    /**
     * Validate JWT token and ensure user is authenticated
     * @param authHeader Authorization header containing Bearer token
     * @return User entity if valid, throws exception otherwise
     */
    protected User validateUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header is required. Please provide a valid Bearer token.");
        }

        if (jwtService == null || userService == null) {
            throw new RuntimeException("JWT service not available");
        }

        String token = jwtService.getTokenFromAuthHeader(authHeader);
        if (token == null) {
            throw new RuntimeException("Invalid authorization header format");
        }

        // Get user email from token
        String userEmail;
        try {
            // Try to get from SecurityContext first (if Spring Security has processed it)
            String emailFromContext = jwtService.getCurrentUserId();
            
            // If not available, parse token directly
            if (emailFromContext != null && !emailFromContext.isEmpty()) {
                userEmail = emailFromContext;
            } else {
                com.nimbusds.jwt.JWTClaimsSet claims = jwtService.getClaimsFromToken(token);
                userEmail = claims.getSubject();
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }

        if (userEmail == null || userEmail.isEmpty()) {
            throw new RuntimeException("Invalid token: user email not found");
        }

        final String finalUserEmail = userEmail;
        return userService.findByEmail(finalUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + finalUserEmail));
    }

    /**
     * Validate JWT token and ensure user has ADMIN role
     * @param authHeader Authorization header containing Bearer token
     * @return User entity if valid ADMIN, throws exception otherwise
     */
    protected User validateAdminUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = validateUser(authHeader);
        
        if (user.getRole() == null || !user.getRole().contains("ADMIN")) {
            throw new RuntimeException("Access denied. Admin role required. Current role: " + user.getRole());
        }

        return user;
    }

    /**
     * Convert Entity to ResponseDto
     */
    protected abstract ResponseDto toResponseDto(T entity);

    /**
     * Convert RequestDto to Entity
     */
    protected abstract T toEntity(RequestDto requestDto);

    /**
     * Convert Page of Entities to Page of ResponseDtos
     */
    protected abstract Page<ResponseDto> convertPage(Page<T> entityPage);

    /**
     * Get entity name for error messages
     */
    protected abstract String getEntityName();

    /**
     * Create new entity
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<ResponseDto>> create(
            @Valid @RequestBody RequestDto requestDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Validate JWT and admin role
            validateUser(authHeader);
            
            T entity = toEntity(requestDto);
            T createdEntity = baseService.create(entity);
            ResponseDto responseDto = toResponseDto(createdEntity);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<ResponseDto>builder()
                            .status(201)
                            .message(getEntityName() + " created successfully")
                            .error(null)
                            .data(responseDto)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<ResponseDto>builder()
                            .status(500)
                            .message("Failed to create " + getEntityName())
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Get entity by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<ResponseDto>> getById(@PathVariable ID id) {
        try {
            Optional<T> entity = baseService.getOne(id);
            if (entity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<ResponseDto>builder()
                                .status(404)
                                .message(getEntityName() + " not found")
                                .error(getEntityName() + " with id " + id + " not found")
                                .data(null)
                                .build());
            }

            ResponseDto responseDto = toResponseDto(entity.get());
            return ResponseEntity.ok(ResponseData.<ResponseDto>builder()
                    .status(200)
                    .message(getEntityName() + " found")
                    .error(null)
                    .data(responseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<ResponseDto>builder()
                            .status(500)
                            .message("Failed to get " + getEntityName())
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Update entity by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<ResponseDto>> update(
            @PathVariable ID id, 
            @Valid @RequestBody RequestDto requestDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Validate JWT and admin role
            validateUser(authHeader);
            
            Optional<T> existingEntity = baseService.getOne(id);
            if (existingEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<ResponseDto>builder()
                                .status(404)
                                .message(getEntityName() + " not found")
                                .error(getEntityName() + " with id " + id + " not found")
                                .data(null)
                                .build());
            }

            T entity = toEntity(requestDto);
            T updatedEntity = baseService.update(id, entity);
            ResponseDto responseDto = toResponseDto(updatedEntity);

            return ResponseEntity.ok(ResponseData.<ResponseDto>builder()
                    .status(200)
                    .message(getEntityName() + " updated successfully")
                    .error(null)
                    .data(responseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<ResponseDto>builder()
                            .status(500)
                            .message("Failed to update " + getEntityName())
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Delete entity by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> delete(@PathVariable ID id,
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = validateUser(authHeader);

            Optional<T> entity = baseService.getOne(id);
            if (entity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<Void>builder()
                                .status(404)
                                .message(getEntityName() + " not found")
                                .error(getEntityName() + " with id " + id + " not found")
                                .data(null)
                                .build());
            }

            baseService.delete(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(200)
                    .message(getEntityName() + " deleted successfully")
                    .error(null)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Void>builder()
                            .status(500)
                            .message("Failed to delete " + getEntityName())
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Change entity status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<ResponseDto>> changeStatus(@PathVariable ID id, @RequestParam Integer status) {
        try {
            Optional<T> entity = baseService.getOne(id);
            if (entity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<ResponseDto>builder()
                                .status(404)
                                .message(getEntityName() + " not found")
                                .error(getEntityName() + " with id " + id + " not found")
                                .data(null)
                                .build());
            }

            T updatedEntity = baseService.changeStatus(id, status);
            ResponseDto responseDto = toResponseDto(updatedEntity);

            return ResponseEntity.ok(ResponseData.<ResponseDto>builder()
                    .status(200)
                    .message(getEntityName() + " status updated successfully")
                    .error(null)
                    .data(responseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<ResponseDto>builder()
                            .status(500)
                            .message("Failed to update " + getEntityName() + " status")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Get all entities
     */
    @GetMapping
    public ResponseEntity<ResponseData<List<ResponseDto>>> getAll() {
        try {
            List<T> entities = baseService.getAll();
            List<ResponseDto> responseDtos = entities.stream()
                    .map(this::toResponseDto)
                    .toList();

            return ResponseEntity.ok(ResponseData.<List<ResponseDto>>builder()
                    .status(200)
                    .message(getEntityName() + "s retrieved successfully")
                    .error(null)
                    .data(responseDtos)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<ResponseDto>>builder()
                            .status(500)
                            .message("Failed to get " + getEntityName() + "s")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Filter entities with pagination and sorting
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<ResponseDto>>> filter(@RequestBody BaseFilterRequest filter) {
        try {
            Page<T> result = baseService.filter(filter);
            Page<ResponseDto> responsePage = convertPage(result);

            return ResponseEntity.ok(ResponseData.<Page<ResponseDto>>builder()
                    .status(200)
                    .message(getEntityName() + "s filtered successfully")
                    .error(null)
                    .data(responsePage)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Page<ResponseDto>>builder()
                            .status(500)
                            .message("Failed to filter " + getEntityName() + "s")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Get entities count
     */
    @GetMapping("/count")
    public ResponseEntity<ResponseData<Long>> getCount() {
        try {
            long count = baseService.count();
            return ResponseEntity.ok(ResponseData.<Long>builder()
                    .status(200)
                    .message(getEntityName() + "s count retrieved successfully")
                    .error(null)
                    .data(count)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Long>builder()
                            .status(500)
                            .message("Failed to get " + getEntityName() + "s count")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Check if entity exists
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<ResponseData<Boolean>> exists(@PathVariable ID id) {
        try {
            boolean exists = baseService.exists(id);
            return ResponseEntity.ok(ResponseData.<Boolean>builder()
                    .status(200)
                    .message(getEntityName() + " existence checked")
                    .error(null)
                    .data(exists)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Boolean>builder()
                            .status(500)
                            .message("Failed to check " + getEntityName() + " existence")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    @PutMapping("/{id}/update-map")
    public ResponseEntity<ResponseData<ResponseDto>> updateMap(@PathVariable ID id, @RequestBody Map<String, Object> map) {
        try {
            T updatedEntity = baseService.updateFromMap(id, map);
            return ResponseEntity.ok(ResponseData.<ResponseDto>builder()
                            .status(200)
                            .message("Map updated successfully")
                            .error(null)
                            .data(toResponseDto(updatedEntity))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<ResponseDto>builder()
                            .status(500)
                            .message("Failed to check " + getEntityName() + " existence")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
