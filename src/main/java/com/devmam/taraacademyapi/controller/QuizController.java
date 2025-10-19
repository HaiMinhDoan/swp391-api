package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.QuizRequestDto;
import com.devmam.taraacademyapi.models.dto.response.QuizResponseDto;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.service.impl.entities.LessonService;
import com.devmam.taraacademyapi.service.impl.entities.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/quizzes")
@PreAuthorize("permitAll()")
public class QuizController extends BaseController<Quiz, Integer, QuizRequestDto, QuizResponseDto> {

    @Autowired
    private LessonService lessonService;

    public QuizController(QuizService quizService) {
        super(quizService);
    }

    @Override
    protected QuizResponseDto toResponseDto(Quiz quiz) {
        return QuizResponseDto.toDTO(quiz);
    }

    @Override
    protected Quiz toEntity(QuizRequestDto requestDto) {
        // Get lesson entity
        Lesson lesson = null;
        if (requestDto.getLessonId() != null) {
            lesson = lessonService.getOne(requestDto.getLessonId()).orElse(null);
        }

        Quiz quiz = new Quiz();
        quiz.setLesson(lesson);
        quiz.setType(requestDto.getType());
        quiz.setQuestion(requestDto.getQuestion());
        quiz.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        quiz.setIsDeleted(0);
        quiz.setCreatedAt(Instant.now());
        quiz.setUpdatedAt(Instant.now());

        return quiz;
    }

    @Override
    protected Page<QuizResponseDto> convertPage(Page<Quiz> quizPage) {
        return QuizResponseDto.convertPage(quizPage);
    }

    @Override
    protected String getEntityName() {
        return "Quiz";
    }
}
