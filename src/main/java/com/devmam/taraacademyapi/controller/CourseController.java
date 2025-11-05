package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.CourseRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CourseResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.CourseCategory;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CourseCategoryService;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
@PreAuthorize("permitAll()")
public class CourseController extends BaseController<Course, Integer, CourseRequestDto, CourseResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Autowired
    private JwtService jwtService;

    public CourseController(CourseService courseService) {
        super(courseService);
    }

    @Override
    protected CourseResponseDto toResponseDto(Course course) {
        return CourseResponseDto.toDto(course);
    }

    @Override
    protected Course toEntity(CourseRequestDto requestDto) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        // Get category entity
        CourseCategory category = null;
        if (requestDto.getCategoryId() != null) {
            category = courseCategoryService.getOne(requestDto.getCategoryId()).orElse(null);
        }

        Course course = new Course();
        course.setCategory(category);
        course.setThumbnail(requestDto.getThumnail());
        course.setTitle(requestDto.getTitle());
        course.setSummary(requestDto.getSummary());
        course.setDescription(requestDto.getDescription());
        course.setLang(requestDto.getLang());
        course.setPrice(requestDto.getPrice());
        course.setSaleOff(requestDto.getSaleOff() != null ? requestDto.getSaleOff() : 0);
        course.setCreatedBy(currentUser);
        course.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        course.setIsDeleted(0);
        course.setCreatedAt(Instant.now());
        course.setUpdatedAt(Instant.now());

        return course;
    }

    @Override
    protected Page<CourseResponseDto> convertPage(Page<Course> coursePage) {
        return CourseResponseDto.convertPage(coursePage);
    }

    @Override
    protected String getEntityName() {
        return "Course";
    }

    /**
     * Get courses by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseData<List<CourseResponseDto>>> getCoursesByStatus(@PathVariable Integer status) {
        List<Course> courses = ((CourseService) baseService).findByStatus(status);
        List<CourseResponseDto> responseDtos = courses.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<CourseResponseDto>>builder()
                .status(200)
                .message("Courses retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Search courses by title
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseData<List<CourseResponseDto>>> searchCoursesByTitle(@RequestParam String title) {
        List<Course> courses = ((CourseService) baseService).findByTitleContaining(title);
        List<CourseResponseDto> responseDtos = courses.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<CourseResponseDto>>builder()
                .status(200)
                .message("Courses retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Get courses by user ID - This method is disabled since Course entity doesn't have user field
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseData<List<CourseResponseDto>>> getCoursesByUserId(@PathVariable UUID userId) {
        // Since Course entity doesn't have user field, return empty list
        return ResponseEntity.ok(ResponseData.<List<CourseResponseDto>>builder()
                .status(200)
                .message("User courses retrieved successfully")
                .error(null)
                .data(List.of())
                .build());
    }

    /**
     * Get courses by category ID
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<List<CourseResponseDto>>> getCoursesByCategoryId(@PathVariable Integer categoryId) {
        List<Course> courses = ((CourseService) baseService).findByCategoryId(categoryId);
        List<CourseResponseDto> categoryCourses = courses.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<CourseResponseDto>>builder()
                .status(200)
                .message("Category courses retrieved successfully")
                .error(null)
                .data(categoryCourses)
                .build());
    }
}
