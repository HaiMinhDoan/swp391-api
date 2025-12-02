package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.UserRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.TranResponseDto;
import com.devmam.taraacademyapi.models.dto.response.UserResponseDto;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.TranService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER");
        return ResponseEntity.ok(ResponseData.<List<String>>builder()
                .status(200)
                .message("Roles retrieved successfully")
                .error(null)
                .data(roles)
                .build());
    }
}