package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.QuizSubmissionRequestDto;
import com.devmam.taraacademyapi.models.dto.response.QuizSubmissionResponseDto;
import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.models.entities.QuizSubmission;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.QuizService;
import com.devmam.taraacademyapi.service.impl.entities.QuizSubmissionService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/quiz-submissions")
@PreAuthorize("permitAll()")
public class QuizSubmissionController extends BaseController<QuizSubmission, Integer, QuizSubmissionRequestDto, QuizSubmissionResponseDto> {

    @Autowired
    private QuizService quizService;

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
        // Get quiz and user entities
        Quiz quiz = null;
        if (requestDto.getQuizId() != null) {
            quiz = quizService.getOne(requestDto.getQuizId()).orElse(null);
        }

        User user = null;
        if (requestDto.getUserId() != null) {
            user = userService.getOne(requestDto.getUserId()).orElse(null);
        }

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuiz(quiz);
        quizSubmission.setUser(user);
//        quizSubmission.setAnswers(requestDto.getAnswers());
//        quizSubmission.setScore(requestDto.getScore());
        quizSubmission.setSubmittedAt(requestDto.getSubmittedAt() != null ? requestDto.getSubmittedAt() : Instant.now());
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
