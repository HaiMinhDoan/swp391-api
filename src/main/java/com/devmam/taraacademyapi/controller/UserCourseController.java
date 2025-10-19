package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.UserCourseRequestDto;
import com.devmam.taraacademyapi.models.dto.response.UserCourseResponseDto;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.models.entities.UserCourse;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserCourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/user-courses")
@PreAuthorize("permitAll()")
public class UserCourseController extends BaseController<UserCourse, Integer, UserCourseRequestDto, UserCourseResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    public UserCourseController(UserCourseService userCourseService) {
        super(userCourseService);
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
}
