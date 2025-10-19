package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.UserResponseDto;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;


    @PreAuthorize( "hasRole('ROLE_USER')")
    @GetMapping("/profile")
    public ResponseEntity<ResponseData<UserResponseDto>> getProfile(HttpServletRequest request) {
        String token = jwtService.getTokenFromAuthHeader(request.getHeader("Authorization"));
        UUID id = jwtService.getUserId(token);
        Optional<User> findingUser = userService.getOne(id);
        if (findingUser.isEmpty()) {
            throw new CommonException("User not found");
        }
        UserResponseDto userResponseDto = UserResponseDto.toDTO(findingUser.get());
        return ResponseEntity.ok(ResponseData.<UserResponseDto>builder()
                .status(200)
                .message("User profile retrieved successfully")
                .error(null)
                .data(userResponseDto)
                .build());
    }

}
