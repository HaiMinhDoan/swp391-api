package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.LoginDTO;
import com.devmam.taraacademyapi.models.dto.request.RegisterDTO;
import com.devmam.taraacademyapi.models.dto.response.AuthenticationResponse;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register-teacher")
    public ResponseEntity<ResponseData<Void>> registerTeacher(@Valid RegisterDTO dto) {
        Optional<User> existingUser = userService.findByUsernameOrEmailOrPhone(dto.getUsername(), dto.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = RegisterDTO.toEntity(dto);
        user.setRole("ROLE_TEACHER");
        user = userService.create(user);
        return ResponseEntity.ok()
                .body(ResponseData.<Void>builder()
                        .status(200)
                        .message("User registered successfully")
                        .error(null)
                        .data(null)
                        .build());
    }

    @PostMapping("/register-student")
    public ResponseEntity<ResponseData<Void>> registerStudent(@Valid RegisterDTO dto) {
        Optional<User> existingUser = userService.findByUsernameOrEmailOrPhone(dto.getUsername(), dto.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = RegisterDTO.toEntity(dto);
        user.setRole("ROLE_STUDENT");
        user = userService.create(user);
        return ResponseEntity.ok()
                .body(ResponseData.<Void>builder()
                        .status(200)
                        .message("User registered successfully")
                        .error(null)
                        .data(null)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseData<AuthenticationResponse>> login(@Valid LoginDTO dto) throws AuthenticationException {
        AuthenticationResponse auth = userService.authenticate(dto.getUsernameOrEmail(), dto.getPassword(), dto.getUserAgent());
        if (!auth.isAuthenticated()) {
            throw new RuntimeException(auth.getMessage());
        }
        return ResponseEntity.ok()
                .body(ResponseData.<AuthenticationResponse>builder()
                        .status(200)
                        .message(auth.getMessage())
                        .error(null)
                        .data(auth)
                        .build());
    }
}
