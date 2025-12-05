package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.EmailHistoryRequestDto;
import com.devmam.taraacademyapi.models.dto.response.EmailHistoryResponseDto;
import com.devmam.taraacademyapi.models.entities.Application;
import com.devmam.taraacademyapi.models.entities.EmailHistory;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.ApplicationService;
import com.devmam.taraacademyapi.service.impl.entities.EmailHistoryService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/email-history")
@PreAuthorize("permitAll()")
public class EmailHistoryController extends BaseController<EmailHistory, Integer, EmailHistoryRequestDto, EmailHistoryResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private JwtService jwtService;

    public EmailHistoryController(EmailHistoryService emailHistoryService) {
        super(emailHistoryService);
    }

    @Override
    protected EmailHistoryResponseDto toResponseDto(EmailHistory emailHistory) {
        return EmailHistoryResponseDto.toDTO(emailHistory);
    }

    @Override
    protected EmailHistory toEntity(EmailHistoryRequestDto requestDto) {
        EmailHistory emailHistory = new EmailHistory();
        
        emailHistory.setRecipientEmail(requestDto.getRecipientEmail());
        emailHistory.setSubject(requestDto.getSubject());
        emailHistory.setContent(requestDto.getContent());
        emailHistory.setStatus(requestDto.getStatus());
        emailHistory.setErrorMessage(requestDto.getErrorMessage());
        
        // Set created by user
        if (requestDto.getCreatedById() != null) {
            User createdBy = userService.getOne(requestDto.getCreatedById())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + requestDto.getCreatedById()));
            emailHistory.setCreatedBy(createdBy);
        } else {
            // Try to get current user from token
            String currentUserEmail = jwtService.getCurrentUserId();
            if (currentUserEmail != null) {
                userService.findByEmail(currentUserEmail).ifPresent(emailHistory::setCreatedBy);
            }
        }
        
        // Set application
        if (requestDto.getApplyId() != null) {
            Application apply = applicationService.getOne(requestDto.getApplyId())
                    .orElseThrow(() -> new IllegalArgumentException("Application not found with id: " + requestDto.getApplyId()));
            emailHistory.setApply(apply);
        }
        
        emailHistory.setCreatedAt(Instant.now());

        return emailHistory;
    }

    @Override
    protected Page<EmailHistoryResponseDto> convertPage(Page<EmailHistory> emailHistoryPage) {
        return EmailHistoryResponseDto.convertPage(emailHistoryPage);
    }

    @Override
    protected String getEntityName() {
        return "EmailHistory";
    }
}

