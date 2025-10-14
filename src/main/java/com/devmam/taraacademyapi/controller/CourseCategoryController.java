package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.CourseCategoryRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CourseCategoryResponseDto;
import com.devmam.taraacademyapi.models.entities.CourseCategory;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CourseCategoryService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/course-categories")
@PreAuthorize("permitAll()")
public class CourseCategoryController extends BaseController<CourseCategory, Integer, CourseCategoryRequestDto, CourseCategoryResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public CourseCategoryController(CourseCategoryService courseCategoryService) {
        super(courseCategoryService);
    }

    @Override
    protected CourseCategoryResponseDto toResponseDto(CourseCategory courseCategory) {
        return CourseCategoryResponseDto.toDTO(courseCategory);
    }

    @Override
    protected CourseCategory toEntity(CourseCategoryRequestDto requestDto) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        CourseCategory courseCategory = new CourseCategory();
        courseCategory.setName(requestDto.getName());
        courseCategory.setDescription(requestDto.getDescription());
        courseCategory.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        courseCategory.setIsDeleted(0);
        courseCategory.setCreatedBy(currentUser);
        courseCategory.setCreatedAt(Instant.now());
        courseCategory.setUpdatedAt(Instant.now());

        return courseCategory;
    }

    @Override
    protected Page<CourseCategoryResponseDto> convertPage(Page<CourseCategory> courseCategoryPage) {
        return CourseCategoryResponseDto.convertPage(courseCategoryPage);
    }

    @Override
    protected String getEntityName() {
        return "CourseCategory";
    }
}
