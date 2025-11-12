package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.UserCourseRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.UserCourseResponseDto;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.models.entities.UserCourse;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserCourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-courses")
@PreAuthorize("permitAll()")
public class UserCourseController extends BaseController<UserCourse, Integer, UserCourseRequestDto, UserCourseResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    private final UserCourseService userCourseService;

    public UserCourseController(UserCourseService userCourseService) {
        super(userCourseService);
        this.userCourseService = userCourseService;
    }

    @Override
    protected UserCourseResponseDto toResponseDto(UserCourse userCourse) {
        return UserCourseResponseDto.toDTO(userCourse);
    }

    @Override
    protected UserCourse toEntity(UserCourseRequestDto requestDto) {
        // Get user and course entities
        User user = null;
        if (requestDto.getUserId() != null) {
            user = userService.getOne(requestDto.getUserId()).orElse(null);
        }

        Course course = null;
        if (requestDto.getCourseId() != null) {
            course = courseService.getOne(requestDto.getCourseId()).orElse(null);
        }

        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setEnrolledAt(requestDto.getEnrolledAt() != null ? requestDto.getEnrolledAt() : Instant.now());
        userCourse.setCompletedAt(requestDto.getCompletedAt());
//        userCourse.setProgress(requestDto.getProgress() != null ? requestDto.getProgress() : 0);
        userCourse.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        userCourse.setIsDeleted(0);
        userCourse.setCreatedAt(Instant.now());
        userCourse.setUpdatedAt(Instant.now());

        return userCourse;
    }

    @Override
    protected Page<UserCourseResponseDto> convertPage(Page<UserCourse> userCoursePage) {
        return UserCourseResponseDto.convertPage(userCoursePage);
    }

    @Override
    protected String getEntityName() {
        return "UserCourse";
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseData<UserCourseResponseDto>> getDetailByUserIdAndCourseId(
            @RequestParam UUID userId,
            @RequestParam Integer courseId) {
        try {
            Optional<UserCourse> userCourse = userCourseService.findByUserIdAndCourseIdActive(userId, courseId);
            
            if (userCourse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<UserCourseResponseDto>builder()
                                .status(404)
                                .message("UserCourse not found or expired")
                                .error("UserCourse with userId " + userId + " and courseId " + courseId + " not found, expired, or inactive")
                                .data(null)
                                .build());
            }

            UserCourseResponseDto responseDto = toResponseDto(userCourse.get());
            return ResponseEntity.ok(ResponseData.<UserCourseResponseDto>builder()
                    .status(200)
                    .message("UserCourse found successfully")
                    .error(null)
                    .data(responseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<UserCourseResponseDto>builder()
                            .status(500)
                            .message("Failed to get UserCourse detail")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
