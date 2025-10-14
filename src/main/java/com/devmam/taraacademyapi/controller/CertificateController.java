package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.CertificateRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CertificateResponseDto;
import com.devmam.taraacademyapi.models.entities.Certificate;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CertificateService;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/certificates")
@PreAuthorize("permitAll()")
public class CertificateController extends BaseController<Certificate, Integer, CertificateRequestDto, CertificateResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private JwtService jwtService;

    public CertificateController(CertificateService certificateService) {
        super(certificateService);
    }

    @Override
    protected CertificateResponseDto toResponseDto(Certificate certificate) {
        return CertificateResponseDto.toDTO(certificate);
    }

    @Override
    protected Certificate toEntity(CertificateRequestDto requestDto) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        // Get user and course entities
        User user = null;
        if (requestDto.getUserId() != null) {
            user = userService.getOne(requestDto.getUserId()).orElse(null);
        }

        Course course = null;
        if (requestDto.getCourseId() != null) {
            course = courseService.getOne(requestDto.getCourseId()).orElse(null);
        }

        Certificate certificate = new Certificate();
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setImgUrl(requestDto.getImgUrl());
        certificate.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        certificate.setIsDeleted(0);
        certificate.setCreatedBy(currentUser);
        certificate.setCreatedAt(Instant.now());
        certificate.setUpdatedAt(Instant.now());

        return certificate;
    }

    @Override
    protected Page<CertificateResponseDto> convertPage(Page<Certificate> certificatePage) {
        return CertificateResponseDto.convertPage(certificatePage);
    }

    @Override
    protected String getEntityName() {
        return "Certificate";
    }
}
