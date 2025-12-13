package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.AssignRoleRequestDto;
import com.devmam.taraacademyapi.models.dto.request.UserRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.TranResponseDto;
import com.devmam.taraacademyapi.models.dto.response.UserResponseDto;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.TranService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("permitAll()")
public class UserController extends BaseController<User, UUID, UserRequestDto, UserResponseDto> {

    public UserController(UserService userService) {
        super(userService);
    }
    @Autowired
    private TranService tranService;

    @Override
    protected UserResponseDto toResponseDto(User user) {
        return UserResponseDto.toDto(user);
    }

    @Override
    protected User toEntity(UserRequestDto requestDto) {
        User user = new User();
        user.setUsername(requestDto.getUsername());
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        user.setFullName(requestDto.getFullName());
        user.setPhone(requestDto.getPhone());
        user.setRole(requestDto.getRole() != null ? requestDto.getRole() : "USER");
        user.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        user.setIsDeleted(0);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return user;
    }

    @Override
    protected Page<UserResponseDto> convertPage(Page<User> userPage) {
        return UserResponseDto.convertPage(userPage);
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

    @GetMapping("/trans/{userId}")
    public ResponseEntity<ResponseData<List<TranResponseDto>>> getTransByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(
                ResponseData.<List<TranResponseDto>>builder()
                        .status(200)
                        .message("User transactions retrieved successfully")
                        .error(null)
                        .data(TranResponseDto.convertList(tranService.getByUserId(userId)))
                        .build()
        );
    }

    @GetMapping("/roles")
    public ResponseEntity<ResponseData<List<String>>> getRoles() {
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER", "ADMIN", "USER");
        return ResponseEntity.ok(ResponseData.<List<String>>builder()
                .status(200)
                .message("Roles retrieved successfully")
                .error(null)
                .data(roles)
                .build());
    }

    /**
     * Assign role to user
     * PUT /api/v1/users/{userId}/assign-role
     * Only ADMIN can assign roles
     */
    @PutMapping("/{userId}/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<UserResponseDto>> assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignRoleRequestDto requestDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Validate JWT and admin role
            validateAdminUser(authHeader);

            // Get user by ID
            User user = baseService.getOne(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // Check if user is deleted
            if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<UserResponseDto>builder()
                                .status(400)
                                .message("Cannot assign role to deleted user")
                                .error("User is deleted")
                                .data(null)
                                .build());
            }

            // Normalize role format (convert ADMIN to ROLE_ADMIN, USER to ROLE_STUDENT, etc.)
            String normalizedRole = normalizeRole(requestDto.getRole());
            
            // Update user role
            user.setRole(normalizedRole);
            user.setUpdatedAt(Instant.now());
            
            User updatedUser = baseService.update(userId, user);
            UserResponseDto responseDto = toResponseDto(updatedUser);

            return ResponseEntity.ok(ResponseData.<UserResponseDto>builder()
                    .status(200)
                    .message("Role assigned successfully")
                    .error(null)
                    .data(responseDto)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.<UserResponseDto>builder()
                            .status(400)
                            .message("Failed to assign role")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<UserResponseDto>builder()
                            .status(500)
                            .message("Failed to assign role")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Normalize role format
     * Converts ADMIN -> ROLE_ADMIN, USER -> ROLE_STUDENT, etc.
     */
    private String normalizeRole(String role) {
        if (role == null || role.isEmpty()) {
            return "ROLE_STUDENT";
        }
        
        // If already in ROLE_ format, return as is
        if (role.startsWith("ROLE_")) {
            return role;
        }
        
        // Convert to ROLE_ format
        switch (role.toUpperCase()) {
            case "ADMIN":
                return "ROLE_ADMIN";
            case "USER":
            case "STUDENT":
                return "ROLE_STUDENT";
            case "TEACHER":
                return "ROLE_TEACHER";
            default:
                return "ROLE_" + role.toUpperCase();
        }
    }
}