package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.QuizAnswerRequestDto;
import com.devmam.taraacademyapi.models.dto.response.QuizAnswerResponseDto;
import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.models.entities.QuizAnswer;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.QuizAnswerService;
import com.devmam.taraacademyapi.service.impl.entities.QuizService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/quiz-answers")
@PreAuthorize("permitAll()")
public class QuizAnswerController extends BaseController<QuizAnswer, Integer, QuizAnswerRequestDto, QuizAnswerResponseDto> {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public QuizAnswerController(QuizAnswerService quizAnswerService) {
        super(quizAnswerService);
    }

    @Override
    protected QuizAnswerResponseDto toResponseDto(QuizAnswer quizAnswer) {
        return QuizAnswerResponseDto.toDTO(quizAnswer);
    }

    @Override
    protected QuizAnswer toEntity(QuizAnswerRequestDto requestDto) {
        // Get quiz and user entities
        Quiz quiz = null;
        if (requestDto.getQuizId() != null) {
            quiz = quizService.getOne(requestDto.getQuizId()).orElse(null);
        }

        User user = null;
        if (requestDto.getUserId() != null) {
            user = userService.getOne(requestDto.getUserId()).orElse(null);
        }

        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setQuestion(quiz);
//        quizAnswer.setUser(user);
        quizAnswer.setAnswerText(requestDto.getAnswer());
        quizAnswer.setIsCorrect(requestDto.getIsCorrect());
        quizAnswer.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        quizAnswer.setIsDeleted(0);
        quizAnswer.setCreatedAt(Instant.now());
        quizAnswer.setUpdatedAt(Instant.now());

        return quizAnswer;
    }

    @Override
    protected Page<QuizAnswerResponseDto> convertPage(Page<QuizAnswer> quizAnswerPage) {
        return QuizAnswerResponseDto.convertPage(quizAnswerPage);
    }

    @Override
    protected String getEntityName() {
        return "QuizAnswer";
    }
}
