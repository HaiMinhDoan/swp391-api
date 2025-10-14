package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.TeacherCvRequestDto;
import com.devmam.taraacademyapi.models.dto.response.TeacherCvResponseDto;
import com.devmam.taraacademyapi.models.entities.TeacherCv;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.TeacherCvService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/teacher-cvs")
@PreAuthorize("permitAll()")
public class TeacherCvController extends BaseController<TeacherCv, Integer, TeacherCvRequestDto, TeacherCvResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public TeacherCvController(TeacherCvService teacherCvService) {
        super(teacherCvService);
    }

    @Override
    protected TeacherCvResponseDto toResponseDto(TeacherCv teacherCv) {
        return TeacherCvResponseDto.toDTO(teacherCv);
    }

    @Override
    protected TeacherCv toEntity(TeacherCvRequestDto requestDto) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        // Get user entity
        User user = null;
        if (requestDto.getUserId() != null) {
            user = userService.getOne(requestDto.getUserId()).orElse(null);
        }

        TeacherCv teacherCv = new TeacherCv();
        teacherCv.setUser(user);
        teacherCv.setCvUrl(requestDto.getCvUrl());
        teacherCv.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        teacherCv.setIsDeleted(0);
        teacherCv.setCreatedBy(currentUser);
        teacherCv.setCreatedAt(Instant.now());
        teacherCv.setUpdatedAt(Instant.now());

        return teacherCv;
    }

    @Override
    protected Page<TeacherCvResponseDto> convertPage(Page<TeacherCv> teacherCvPage) {
        return TeacherCvResponseDto.convertPage(teacherCvPage);
    }

    @Override
    protected String getEntityName() {
        return "TeacherCv";
    }
}
