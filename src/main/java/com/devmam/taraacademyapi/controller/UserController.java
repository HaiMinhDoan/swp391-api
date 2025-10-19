package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.UserRequestDto;
import com.devmam.taraacademyapi.models.dto.response.UserResponseDto;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("permitAll()")
public class UserController extends BaseController<User, UUID, UserRequestDto, UserResponseDto> {

    public UserController(UserService userService) {
        super(userService);
    }

    @Override
    protected UserResponseDto toResponseDto(User user) {
        return UserResponseDto.toDTO(user);
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
}
