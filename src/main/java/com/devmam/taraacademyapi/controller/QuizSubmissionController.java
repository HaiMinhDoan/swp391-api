package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.QuizSubmissionRequestDto;
import com.devmam.taraacademyapi.models.dto.response.QuizSubmissionResponseDto;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.QuizSubmission;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.LessonService;
import com.devmam.taraacademyapi.service.impl.entities.QuizSubmissionService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/quiz-submissions")
@PreAuthorize("permitAll()")
public class QuizSubmissionController extends BaseController<QuizSubmission, Integer, QuizSubmissionRequestDto, QuizSubmissionResponseDto> {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private UserService userService;

    public QuizSubmissionController(QuizSubmissionService quizSubmissionService) {
        super(quizSubmissionService);
    }

    @Override
    protected QuizSubmissionResponseDto toResponseDto(QuizSubmission quizSubmission) {
        return QuizSubmissionResponseDto.toDTO(quizSubmission);
    }

    @Override
    protected QuizSubmission toEntity(QuizSubmissionRequestDto requestDto) {
        // Get lesson and user entities
        Lesson lesson = null;
        if (requestDto.getLessonId() != null) {
            lesson = lessonService.getOne(requestDto.getLessonId()).orElse(null);
        }

        User user = null;
        if (requestDto.getUserId() != null) {
            user = userService.getOne(requestDto.getUserId()).orElse(null);
        }

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setLesson(lesson);
        quizSubmission.setUser(user);
        quizSubmission.setStartedAt(requestDto.getStartedAt() != null ? requestDto.getStartedAt() : Instant.now());
        quizSubmission.setSubmittedAt(requestDto.getSubmittedAt());
        quizSubmission.setScore(requestDto.getScore());
        quizSubmission.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        quizSubmission.setIsDeleted(0);
        quizSubmission.setCreatedAt(Instant.now());
        quizSubmission.setUpdatedAt(Instant.now());

        return quizSubmission;
    }

    @Override
    protected Page<QuizSubmissionResponseDto> convertPage(Page<QuizSubmission> quizSubmissionPage) {
        return QuizSubmissionResponseDto.convertPage(quizSubmissionPage);
    }

    @Override
    protected String getEntityName() {
        return "QuizSubmission";
    }
}
