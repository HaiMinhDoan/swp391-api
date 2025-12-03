package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.dto.request.ChangePasswordRequest;
import com.devmam.taraacademyapi.models.dto.request.UpdateProfileRequest;
import com.devmam.taraacademyapi.models.dto.response.*;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.UserCourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserCourseService userCourseService;


    @GetMapping("/profile")
    public ResponseEntity<ResponseData<UserResponseDto>> getProfile(HttpServletRequest request) {

        UUID id = getUserIdFromRequest(request);
        Optional<User> findingUser = userService.getOne(id);
        if (findingUser.isEmpty()) {
            throw new CommonException("User not found");
        }
        UserResponseDto userResponseDto = UserResponseDto.toDto(findingUser.get());
        return ResponseEntity.ok(ResponseData.<UserResponseDto>builder()
                .status(200)
                .message("User profile retrieved successfully")
                .error(null)
                .data(userResponseDto)
                .build());
    }

    @PostMapping("/profile/change-password")
    public ResponseEntity<ResponseData<Void>> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest requestBody) {
        UUID id = getUserIdFromRequest(request);
        Optional<User> findingUser = userService.getOne(id);
        if (findingUser.isEmpty()) {
            throw new CommonException("User not found");
        }
        User user = findingUser.get();
        if (!passwordEncoder.matches(requestBody.getOldPassword(), user.getPassword())) {
            throw new CommonException("Old password is not correct");
        }
        user.setPassword(passwordEncoder.encode(requestBody.getNewPassword()));
        userService.update(id, user);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Password changed successfully")
                .error(null)
                .data(null)
                .build());
    }

    @GetMapping("/profile/user-courses")
    public ResponseEntity<ResponseData<List<HistoryUserCourse>>> getUserCourses(HttpServletRequest request) {

        UUID id = getUserIdFromRequest(request);
        List<HistoryUserCourse> results = HistoryUserCourse.convertList(userCourseService.getByUserId(id));
        return ResponseEntity.ok(ResponseData.<List<HistoryUserCourse>>builder()
                .status(200)
                .message("User courses retrieved successfully")
                .error(null)
                .data(results)
                .build());
    }

    @PutMapping("/profile")
    public ResponseEntity<ResponseData<UserResponseDto>> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateProfileRequest updateRequest) {
        try {
            UUID id = getUserIdFromRequest(request);
            Optional<User> findingUser = userService.getOne(id);
            
            if (findingUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<UserResponseDto>builder()
                                .status(404)
                                .message("User not found")
                                .error("User with id " + id + " not found")
                                .data(null)
                                .build());
            }

            User user = findingUser.get();
            
            // Update username (nếu có và khác với username hiện tại)
            if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(user.getUsername())) {
                // Kiểm tra username đã tồn tại chưa (trừ chính user hiện tại)
                Optional<User> existingUser = userService.findByUsernameOrEmailOrPhone(updateRequest.getUsername(), updateRequest.getUsername());
                if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseData.<UserResponseDto>builder()
                                    .status(400)
                                    .message("Username already exists")
                                    .error("Username " + updateRequest.getUsername() + " is already in use")
                                    .data(null)
                                    .build());
                }
                user.setUsername(updateRequest.getUsername());
            }
            
            // Update email (nếu có và khác với email hiện tại)
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
                // Kiểm tra email đã tồn tại chưa (trừ chính user hiện tại)
                Optional<User> existingUser = userService.findByEmail(updateRequest.getEmail());
                if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseData.<UserResponseDto>builder()
                                    .status(400)
                                    .message("Email already exists")
                                    .error("Email " + updateRequest.getEmail() + " is already in use")
                                    .data(null)
                                    .build());
                }
                user.setEmail(updateRequest.getEmail());
            }
            
            // Update fullName
            if (updateRequest.getFullName() != null) {
                user.setFullName(updateRequest.getFullName());
            }
            
            // Update phone
            if (updateRequest.getPhone() != null) {
                user.setPhone(updateRequest.getPhone());
            }
            
            // Update customerCode
            if (updateRequest.getCustomerCode() != null) {
                user.setCustomerCode(updateRequest.getCustomerCode());
            }
            
            // Update accountBalance
            if (updateRequest.getAccountBalance() != null) {
                user.setAccountBalance(updateRequest.getAccountBalance());
            }
            
            // Update avt (avatar)
            if (updateRequest.getAvt() != null) {
                user.setAvt(updateRequest.getAvt());
            }
            
            // Update role
            if (updateRequest.getRole() != null) {
                user.setRole(updateRequest.getRole());
            }
            
            // Tự động cập nhật updatedAt (không cho phép client set)
            user.setUpdatedAt(Instant.now());
            
            // Lưu thay đổi
            User updatedUser = userService.update(id, user);
            UserResponseDto userResponseDto = UserResponseDto.toDto(updatedUser);
            
            return ResponseEntity.ok(ResponseData.<UserResponseDto>builder()
                    .status(200)
                    .message("Profile updated successfully")
                    .error(null)
                    .data(userResponseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<UserResponseDto>builder()
                            .status(500)
                            .message("Failed to update profile")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    private UUID getUserIdFromRequest(HttpServletRequest request){
        String token = jwtService.getTokenFromAuthHeader(request.getHeader("Authorization"));
        return jwtService.getUserId(token);
    }

}
