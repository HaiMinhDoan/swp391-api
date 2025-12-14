package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.dto.request.*;
import com.devmam.taraacademyapi.models.dto.response.AuthenticationResponse;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.UserResponseDto;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.CalcService;
import com.devmam.taraacademyapi.service.EmailService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CalcService calcService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<ResponseData<User>> registerStudent(@Valid @RequestBody RegisterDTO dto) {
        try {
            Optional<User> existingUser = userService.findByUsernameOrEmailOrPhone(dto.getUsername(), dto.getEmail());
            if (existingUser.isPresent()) {
                throw new RuntimeException("User already exists");
            }
            User user = RegisterDTO.toEntity(dto);
            user.setStatus(0);
            if (dto.getRoleType() == 1) {
                user.setRole("ROLE_TEACHER");
            } else {
                user.setRole("ROLE_STUDENT");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            String opt = calcService.getRandomActiveCode(6l);
            user.setOtp(opt);
            user = userService.create(user);
            Map<String, Object> model = Map.of("activationCode", opt);

            emailService.sendHtmlEmailFromTemplate(user.getEmail(), "Kích hoạt tài khoản ngay", "activation.html", model);

            return ResponseEntity.ok()
                    .body(ResponseData.<User>builder()
                            .status(200)
                            .message("User registered successfully")
                            .error(null)
                            .data(user)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ResponseData.<User>builder()
                            .status(500)
                            .message("Failed to register user")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseData<UserResponseDto>> verifyAccount(@Valid @RequestBody VerifyDto dto) {
        List<User> users = userService.getUserByOtp(dto.getOtp());

        if (users.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ResponseData.<UserResponseDto>builder()
                            .status(404)
                            .message("Otp không hợp lệ")
                            .error("Otp không hợp lệ")
                            .data(null)
                            .build());
        }

        for (User user : users) {
            user.setStatus(1);
            userService.update(user.getId(), user);
        }

        return ResponseEntity.ok()
                .body(ResponseData.<UserResponseDto>builder()
                        .status(200)
                        .message("Account verified successfully")
                        .error(null)
                        .data(UserResponseDto.toDto(users.get(0)))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseData<AuthenticationResponse>> login(@Valid @RequestBody LoginDTO dto) throws AuthenticationException {
        AuthenticationResponse auth = userService.authenticate(dto.getUsernameOrEmail(), dto.getPassword(), "Chrome");
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

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseData<String>> forgotPassword(@Valid @RequestBody ForgotPasswordDto dto) {
        Optional<User> findingUser = userService.findByEmail(dto.getEmail());

        if (findingUser.isEmpty()) {
            throw new CommonException("Email không tồn tại trong hệ thống");
        }

        User user = findingUser.get();

        String opt = calcService.getRandomActiveCode(6l);
        String resetUrl = "http://localhost:3000/reset-password?otp=" + opt;
        findingUser.get().setOtp(opt);
        userService.update(findingUser.get().getId(), findingUser.get());
        Map<String, Object> model = new HashMap<>();
        model.put("resetUrl", resetUrl);
        model.put("userName", user.getUsername());
        emailService.sendHtmlEmailFromTemplate(user.getEmail(), "Thay đổi mật khẩu", "reset-password.html", model);

        return ResponseEntity.ok(
                ResponseData.<String>builder()
                        .status(200)
                        .message("Đường dẫn đổi mật khẩu đã được gửi thành công")
                        .error(null)
                        .data("Đường dẫn đổi mật khẩu đã được gửi thành công")
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData<String>> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        List<User> users = userService.getUserByOtp(dto.getOtp());
        if (users.isEmpty()) {
            throw new CommonException("Đường dẫn đã hết hạn");
        }

        for (User user : users) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userService.update(user.getId(), user);
        }

        return ResponseEntity.ok().body(
                ResponseData.<String>builder()
                        .status(200)
                        .message("Mật khẩu đã được đổi thành công")
                        .error(null)
                        .data("Mật khẩu đã được đổi thành công")
                        .build()
        );
    }
}
