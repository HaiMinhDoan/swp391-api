package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.dto.request.CourseCartRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CourseCartResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.CourseCart;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CourseCartService;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-carts")
public class CourseCartController extends BaseController<CourseCart, Integer, CourseCartRequestDto, CourseCartResponseDto>{

    @Autowired
    private CourseCartService courseCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private JwtService jwtService;

    public CourseCartController(CourseCartService courseCartService) {
        super(courseCartService);
    }


    @PostMapping("/add")
    public ResponseEntity<ResponseData<CourseCartResponseDto>> addCourseToCart(
            @RequestBody CourseCartRequestDto courseCartRequestDto,
            @RequestHeader(value = "Authorization",required = false) String authHeader
    ) {
        String token = jwtService.getTokenFromAuthHeader(authHeader);
        UUID userId = jwtService.getUserId(token);

        Optional<User> findingUser = userService.getOne(userId);

        if (findingUser.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        CourseCart courseCart = toEntity(courseCartRequestDto);
        courseCart.setUser(findingUser.get());

        courseCart = this.courseCartService.create(courseCart);
        if (courseCart == null) {
            throw new CommonException("CourseCart not found");
        }
        return ResponseEntity.ok(
                ResponseData.<CourseCartResponseDto>builder()
                        .status(200)
                        .message("Course added to cart successfully")
                        .error(null)
                        .data(toResponseDto(courseCart))
                        .build()
        );
    }

    @Override
    protected CourseCartResponseDto toResponseDto(CourseCart entity) {
        return CourseCartResponseDto.toDto(entity);
    }

    @Override
    protected CourseCart toEntity(CourseCartRequestDto courseCartRequestDto) {
        Optional<Course> findingCourse = courseService.getOne(courseCartRequestDto.getCourseId());
        if (findingCourse.isEmpty()) {
            throw new EntityNotFoundException("Course not found");
        }
        return CourseCart.builder()
                .course(findingCourse.get())
                .build();
    }

    @Override
    protected Page<CourseCartResponseDto> convertPage(Page<CourseCart> entityPage) {
        return null;
    }

    @Override
    protected String getEntityName() {
        return "";
    }

}
