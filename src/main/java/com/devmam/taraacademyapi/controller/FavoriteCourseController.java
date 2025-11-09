package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.CourseCartRequestDto;
import com.devmam.taraacademyapi.models.dto.request.FavoriteCourseRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CourseCartResponseDto;
import com.devmam.taraacademyapi.models.dto.response.FavoriteCourseResponseDto;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.CourseCart;
import com.devmam.taraacademyapi.models.entities.FavoriteCourse;
import com.devmam.taraacademyapi.service.BaseService;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.FavoriteCourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/favorite-courses")
public class FavoriteCourseController extends BaseController<FavoriteCourse, Integer, FavoriteCourseRequestDto, FavoriteCourseResponseDto>{

    @Autowired
    private FavoriteCourseService favoriteCourseService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private JwtService jwtService;

    public FavoriteCourseController(FavoriteCourseService baseService) {
        super(baseService);
    }

    @Override
    protected FavoriteCourseResponseDto toResponseDto(FavoriteCourse entity) {
        return FavoriteCourseResponseDto.toDto(entity);
    }

    @Override
    protected FavoriteCourse toEntity(FavoriteCourseRequestDto favoriteCourseRequestDto) {
        Optional<Course> findingCourse = courseService.getOne(favoriteCourseRequestDto.getCourseId());
        if (findingCourse.isEmpty()) {
            throw new EntityNotFoundException("Course not found");
        }
        return FavoriteCourse.builder()
                .course(findingCourse.get())
                .build();
    }

    @Override
    protected Page<FavoriteCourseResponseDto> convertPage(Page<FavoriteCourse> entityPage) {
        return FavoriteCourseResponseDto.convertPage(entityPage);
    }

    @Override
    protected String getEntityName() {
        return "FavoriteCourse";
    }
}
