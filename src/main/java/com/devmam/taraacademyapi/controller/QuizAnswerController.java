package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.QuizAnswerRequestDto;
import com.devmam.taraacademyapi.models.dto.response.QuizAnswerResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.models.entities.QuizAnswer;
import com.devmam.taraacademyapi.models.entities.QuizSubmission;
import com.devmam.taraacademyapi.repository.QuizAnswerRepository;
import com.devmam.taraacademyapi.service.impl.entities.QuizAnswerService;
import com.devmam.taraacademyapi.service.impl.entities.QuizService;
import com.devmam.taraacademyapi.service.impl.entities.QuizSubmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/quiz-answers")
@PreAuthorize("permitAll()")
public class QuizAnswerController extends BaseController<QuizAnswer, Integer, QuizAnswerRequestDto, QuizAnswerResponseDto> {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    public QuizAnswerController(QuizAnswerService quizAnswerService) {
        super(quizAnswerService);
    }

    @Override
    protected QuizAnswerResponseDto toResponseDto(QuizAnswer quizAnswer) {
        return QuizAnswerResponseDto.toDTO(quizAnswer);
    }

    @Override
    protected QuizAnswer toEntity(QuizAnswerRequestDto requestDto) {
        // Get quiz and submission entities
        Quiz question = null;
        if (requestDto.getQuestionId() != null) {
            question = quizService.getOne(requestDto.getQuestionId()).orElse(null);
        }

        QuizSubmission submission = null;
        if (requestDto.getSubmissionId() != null) {
            submission = quizSubmissionService.getOne(requestDto.getSubmissionId()).orElse(null);
        }

        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setSubmission(submission);
        quizAnswer.setQuestion(question);
        quizAnswer.setSelectedOptionId(requestDto.getSelectedOptionId());
        quizAnswer.setAnswerText(requestDto.getAnswerText());
        quizAnswer.setIsCorrect(requestDto.getIsCorrect());
        quizAnswer.setTeacherNote(requestDto.getTeacherNote());
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

    /**
     * Create multiple QuizAnswers at once
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseData<List<QuizAnswerResponseDto>>> createBulk(
            @Valid @RequestBody List<QuizAnswerRequestDto> requestDtos) {
        try {
            if (requestDtos == null || requestDtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<List<QuizAnswerResponseDto>>builder()
                                .status(400)
                                .message("Request list cannot be empty")
                                .error("Request list is null or empty")
                                .data(null)
                                .build());
            }

            // Convert all DTOs to entities
            List<QuizAnswer> quizAnswers = requestDtos.stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList());

            // Save all entities at once
            List<QuizAnswer> savedQuizAnswers = quizAnswerRepository.saveAll(quizAnswers);

            // Convert all saved entities to response DTOs
            List<QuizAnswerResponseDto> responseDtos = savedQuizAnswers.stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<List<QuizAnswerResponseDto>>builder()
                            .status(201)
                            .message("Successfully created " + responseDtos.size() + " QuizAnswer(s)")
                            .error(null)
                            .data(responseDtos)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<QuizAnswerResponseDto>>builder()
                            .status(500)
                            .message("Failed to create QuizAnswers")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
