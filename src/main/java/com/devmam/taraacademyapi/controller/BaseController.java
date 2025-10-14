package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.service.BaseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
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

    public BaseController(BaseService<T, ID> baseService) {
        this.baseService = baseService;
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
    public ResponseEntity<ResponseData<ResponseDto>> create(@Valid @RequestBody RequestDto requestDto) {
        try {
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
    public ResponseEntity<ResponseData<ResponseDto>> update(@PathVariable ID id, @Valid @RequestBody RequestDto requestDto) {
        try {
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
    public ResponseEntity<ResponseData<Void>> delete(@PathVariable ID id) {
        try {
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
}
