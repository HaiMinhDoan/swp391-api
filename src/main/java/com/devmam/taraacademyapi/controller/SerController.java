package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.SerRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.SerResponseDto;
import com.devmam.taraacademyapi.models.entities.Ser;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.SerService;
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
@RequestMapping("/api/v1/services")
@PreAuthorize("permitAll()")
public class SerController {

    @Autowired
    private SerService serService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Create a new service
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<SerResponseDto>> createService(@Valid @RequestBody SerRequestDto request) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        Ser ser = Ser.builder()
                .name(request.getName())
                .description(request.getDescription())
                .detail(request.getDetail())
                .price(request.getPrice())
                .thumnail(request.getThumnail())
                .createdBy(currentUser)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .isDeleted(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Ser createdSer = serService.create(ser);
        SerResponseDto responseDto = SerResponseDto.toDTO(createdSer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<SerResponseDto>builder()
                        .status(201)
                        .message("Service created successfully")
                        .error(null)
                        .data(responseDto)
                        .build());
    }

    /**
     * Get service by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<SerResponseDto>> getServiceById(@PathVariable Integer id) {
        Optional<Ser> ser = serService.getOne(id);
        if (ser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SerResponseDto>builder()
                            .status(404)
                            .message("Service not found")
                            .error("Service with id " + id + " not found")
                            .data(null)
                            .build());
        }

        SerResponseDto responseDto = SerResponseDto.toDTO(ser.get());
        return ResponseEntity.ok(ResponseData.<SerResponseDto>builder()
                .status(200)
                .message("Service found")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Update service by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<SerResponseDto>> updateService(
            @PathVariable Integer id, 
            @Valid @RequestBody SerRequestDto request) {
        
        Optional<Ser> existingSer = serService.getOne(id);
        if (existingSer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SerResponseDto>builder()
                            .status(404)
                            .message("Service not found")
                            .error("Service with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Ser ser = existingSer.get();
        ser.setName(request.getName());
        ser.setDescription(request.getDescription());
        ser.setDetail(request.getDetail());
        ser.setPrice(request.getPrice());
        ser.setThumnail(request.getThumnail());
        ser.setStatus(request.getStatus() != null ? request.getStatus() : ser.getStatus());
        ser.setUpdatedAt(Instant.now());

        Ser updatedSer = serService.update(id, ser);
        SerResponseDto responseDto = SerResponseDto.toDTO(updatedSer);

        return ResponseEntity.ok(ResponseData.<SerResponseDto>builder()
                .status(200)
                .message("Service updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Delete service by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteService(@PathVariable Integer id) {
        Optional<Ser> ser = serService.getOne(id);
        if (ser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Void>builder()
                            .status(404)
                            .message("Service not found")
                            .error("Service with id " + id + " not found")
                            .data(null)
                            .build());
        }

        serService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Service deleted successfully")
                .error(null)
                .data(null)
                .build());
    }

    /**
     * Change service status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<SerResponseDto>> changeServiceStatus(
            @PathVariable Integer id, 
            @RequestParam Integer status) {
        
        Optional<Ser> ser = serService.getOne(id);
        if (ser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SerResponseDto>builder()
                            .status(404)
                            .message("Service not found")
                            .error("Service with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Ser updatedSer = serService.changeStatus(id, status);
        SerResponseDto responseDto = SerResponseDto.toDTO(updatedSer);

        return ResponseEntity.ok(ResponseData.<SerResponseDto>builder()
                .status(200)
                .message("Service status updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Get all services
     */
    @GetMapping
    public ResponseEntity<ResponseData<List<SerResponseDto>>> getAllServices() {
        List<Ser> services = serService.getAll();
        List<SerResponseDto> responseDtos = services.stream()
                .map(SerResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<SerResponseDto>>builder()
                .status(200)
                .message("Services retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Filter services with pagination and sorting
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<SerResponseDto>>> filterServices(@RequestBody BaseFilterRequest filter) {
        Page<Ser> result = serService.filter(filter);
        Page<SerResponseDto> responsePage = SerResponseDto.convertPage(result);

        return ResponseEntity.ok(ResponseData.<Page<SerResponseDto>>builder()
                .status(200)
                .message("Services filtered successfully")
                .error(null)
                .data(responsePage)
                .build());
    }

    /**
     * Get services count
     */
    @GetMapping("/count")
    public ResponseEntity<ResponseData<Long>> getServicesCount() {
        long count = serService.count();
        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(200)
                .message("Services count retrieved successfully")
                .error(null)
                .data(count)
                .build());
    }
}
