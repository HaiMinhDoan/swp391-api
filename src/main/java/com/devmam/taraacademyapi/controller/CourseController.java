package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.response.CourseResponseDTO;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/courses")
@PreAuthorize("permitAll()")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<CourseResponseDTO>> getCourseById(@PathVariable Integer id){
        Optional<Course> course = courseService.getOne(id);
        if(course.isEmpty()){
            throw new RuntimeException("course not found");
        }
        return ResponseEntity.ok(ResponseData.<CourseResponseDTO>builder()
                        .status(200)
                        .message("course found")
                        .error(null)
                        .data(CourseResponseDTO.toDTO(course.get()))
                        .build());
    }

    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<CourseResponseDTO>>> filter(@RequestBody BaseFilterRequest filter) {
        Page<Course> result = courseService.filter(filter);

        return ResponseEntity.ok(ResponseData.<Page<CourseResponseDTO>>builder()
                .status(200)
                .data(CourseResponseDTO.convertPage(result))
                .message("Success")
                .build());
    }
}
