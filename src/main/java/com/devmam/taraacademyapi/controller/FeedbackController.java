package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.FeedbackRequestDto;
import com.devmam.taraacademyapi.models.dto.response.FeedbackResponseDto;
import com.devmam.taraacademyapi.models.entities.Feedback;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.FeedbackService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/feedbacks")
@PreAuthorize("permitAll()")
public class FeedbackController extends BaseController<Feedback, Integer, FeedbackRequestDto, FeedbackResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public FeedbackController(FeedbackService feedbackService) {
        super(feedbackService);
    }

    @Override
    protected FeedbackResponseDto toResponseDto(Feedback feedback) {
        return FeedbackResponseDto.toDTO(feedback);
    }

    @Override
    protected Feedback toEntity(FeedbackRequestDto requestDto) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        Feedback feedback = new Feedback();
        feedback.setReferenceType(requestDto.getReferenceType());
        feedback.setReferenceId(requestDto.getReferenceId());
        feedback.setReferenceUserId(requestDto.getReferenceUserId());
        feedback.setUser(currentUser);
        feedback.setRating(requestDto.getRating());
        feedback.setComment(requestDto.getComment());
        feedback.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        feedback.setIsDeleted(0);
        feedback.setCreatedAt(Instant.now());
        feedback.setImgUrl(requestDto.getImgUrl());
        feedback.setUpdatedAt(Instant.now());

        return feedback;
    }

    @Override
    protected Page<FeedbackResponseDto> convertPage(Page<Feedback> feedbackPage) {
        return FeedbackResponseDto.convertPage(feedbackPage);
    }

    @Override
    protected String getEntityName() {
        return "Feedback";
    }
}
