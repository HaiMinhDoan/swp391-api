package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.QuizOptionRequestDto;
import com.devmam.taraacademyapi.models.dto.response.QuizOptionResponseDto;
import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.models.entities.QuizOption;
import com.devmam.taraacademyapi.service.impl.entities.QuizOptionService;
import com.devmam.taraacademyapi.service.impl.entities.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/quiz-options")
@PreAuthorize("permitAll()")
public class QuizOptionController extends BaseController<QuizOption, Integer, QuizOptionRequestDto, QuizOptionResponseDto> {

    @Autowired
    private QuizService quizService;

    public QuizOptionController(QuizOptionService quizOptionService) {
        super(quizOptionService);
    }

    @Override
    protected QuizOptionResponseDto toResponseDto(QuizOption quizOption) {
        return QuizOptionResponseDto.toDTO(quizOption);
    }

    @Override
    protected QuizOption toEntity(QuizOptionRequestDto requestDto) {
        // Get quiz entity
        Quiz quiz = null;
        if (requestDto.getQuizId() != null) {
            quiz = quizService.getOne(requestDto.getQuizId()).orElse(null);
        }

        QuizOption quizOption = new QuizOption();
        quizOption.setQuiz(quiz);
        quizOption.setContent(requestDto.getOptionText());
        quizOption.setIsCorrect(requestDto.getIsCorrect());
//        quizOption.setOrderIndex(requestDto.getOrderIndex() != null ? requestDto.getOrderIndex() : 0);
        quizOption.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        quizOption.setIsDeleted(0);
        quizOption.setCreatedAt(Instant.now());
        quizOption.setUpdatedAt(Instant.now());

        return quizOption;
    }

    @Override
    protected Page<QuizOptionResponseDto> convertPage(Page<QuizOption> quizOptionPage) {
        return QuizOptionResponseDto.convertPage(quizOptionPage);
    }

    @Override
    protected String getEntityName() {
        return "QuizOption";
    }
}
