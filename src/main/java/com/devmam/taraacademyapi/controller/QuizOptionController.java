package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.QuizOptionRequestDto;
import com.devmam.taraacademyapi.models.dto.response.QuizOptionResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.models.entities.QuizOption;
import com.devmam.taraacademyapi.service.impl.entities.QuizOptionService;
import com.devmam.taraacademyapi.service.impl.entities.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/quiz-options")
@PreAuthorize("permitAll()")
public class QuizOptionController extends BaseController<QuizOption, Integer, QuizOptionRequestDto, QuizOptionResponseDto> {

    @Autowired
    private QuizService quizService;
    
    @Autowired
    private QuizOptionService quizOptionService;

    public QuizOptionController(QuizOptionService quizOptionService) {
        super(quizOptionService);
    }
    
    /**
     * Delete all quiz options by quiz id
     */
    @DeleteMapping("/quiz/{quizId}")
    public ResponseEntity<ResponseData<Void>> deleteAllByQuizId(@PathVariable Integer quizId) {
        try {
            // Check if quiz exists
            if (!quizService.exists(quizId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<Void>builder()
                                .status(404)
                                .message("Quiz not found")
                                .error("Quiz with id " + quizId + " not found")
                                .data(null)
                                .build());
            }
            
            // Count quiz options before deletion
            long countBefore = quizOptionService.countByQuizId(quizId);
            
            // Delete all quiz options
            quizOptionService.deleteAllByQuizId(quizId);
            
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(200)
                    .message(String.format("Đã xóa %d quiz option(s) cho quiz id %d", countBefore, quizId))
                    .error(null)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Void>builder()
                            .status(500)
                            .message("Failed to delete quiz options")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
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
