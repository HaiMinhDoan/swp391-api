package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.ApplicationRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ApplicationResponseDto;
import com.devmam.taraacademyapi.models.entities.Application;
import com.devmam.taraacademyapi.models.entities.Career;
import com.devmam.taraacademyapi.service.impl.entities.ApplicationService;
import com.devmam.taraacademyapi.service.impl.entities.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/applications")
@PreAuthorize("permitAll()")
public class ApplicationController extends BaseController<Application, Integer, ApplicationRequestDto, ApplicationResponseDto> {

    @Autowired
    private CareerService careerService;

    public ApplicationController(ApplicationService applicationService) {
        super(applicationService);
    }

    @Override
    protected ApplicationResponseDto toResponseDto(Application application) {
        return ApplicationResponseDto.toDTO(application);
    }

    @Override
    protected Application toEntity(ApplicationRequestDto requestDto) {
        Application application = new Application();
        
        // Set career
        if (requestDto.getCareerId() != null) {
            Career career = careerService.getOne(requestDto.getCareerId())
                    .orElseThrow(() -> new IllegalArgumentException("Career not found with id: " + requestDto.getCareerId()));
            application.setCareer(career);
        }
        
        application.setCvUrl(requestDto.getCvUrl());
        application.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        application.setInterviewDate(requestDto.getInterviewDate());
        application.setNote(requestDto.getNote());
        application.setFinalNote(requestDto.getFinalNote());
        application.setFullName(requestDto.getFullName());
        application.setEmail(requestDto.getEmail());
        application.setPhone(requestDto.getPhone());
        application.setGender(requestDto.getGender());
        application.setInterviewDatetime(requestDto.getInterviewDatetime());
        application.setInterviewType(requestDto.getInterviewType());
        application.setMeetingLink(requestDto.getMeetingLink());
        application.setIsDeleted(0);
        application.setCreatedAt(Instant.now());
        application.setUpdatedAt(Instant.now());

        return application;
    }

    @Override
    protected Page<ApplicationResponseDto> convertPage(Page<Application> applicationPage) {
        return ApplicationResponseDto.convertPage(applicationPage);
    }

    @Override
    protected String getEntityName() {
        return "Application";
    }
}

