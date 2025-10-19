package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.dto.request.ChangePasswordRequest;
import com.devmam.taraacademyapi.models.dto.response.*;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.models.entities.UserCourse;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.UserCourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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


    @PreAuthorize( "hasRole('ROLE_USER')")
    @GetMapping("/profile")
    public ResponseEntity<ResponseData<UserResponseDto>> getProfile(HttpServletRequest request) {

        UUID id = getUserIdFromRequest(request);
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

    @PreAuthorize("hasRole('ROLE_USER')")
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

    @PreAuthorize("hasRole('ROLE_USER')")
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

    private UUID getUserIdFromRequest(HttpServletRequest request){
        String token = jwtService.getTokenFromAuthHeader(request.getHeader("Authorization"));
        return jwtService.getUserId(token);
    }

}
