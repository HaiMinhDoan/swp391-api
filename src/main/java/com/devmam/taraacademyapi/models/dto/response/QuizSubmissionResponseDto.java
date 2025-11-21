package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.QuizSubmission;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.QuizSubmission}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class QuizSubmissionResponseDto implements Serializable {
    private final Integer id;
    private final Integer lessonId;
    private final LessonResponseDto lesson;
    private final UUID userId;
    private final String userUsername;
    private final Instant startedAt;
    private final Instant submittedAt;
    private final BigDecimal score;
    private final List<QuizAnswerResponseDto> answers;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static QuizSubmissionResponseDto toDTO(QuizSubmission quizSubmission) {
        LessonResponseDto lessonDto = null;
        if (quizSubmission.getLesson() != null) {
            lessonDto = LessonResponseDto.toDTO(quizSubmission.getLesson());
        }
        
        List<QuizAnswerResponseDto> answersDto = null;
        if (quizSubmission.getAnswers() != null && !quizSubmission.getAnswers().isEmpty()) {
            // Use toDTO without submission to avoid circular reference
            answersDto = quizSubmission.getAnswers().stream()
                    .map(answer -> QuizAnswerResponseDto.toDTO(answer, false))
                    .collect(Collectors.toList());
        }
        
        return QuizSubmissionResponseDto.builder()
                .id(quizSubmission.getId())
                .lessonId(quizSubmission.getLesson() != null ? quizSubmission.getLesson().getId() : null)
                .lesson(lessonDto)
                .userId(quizSubmission.getUser() != null ? quizSubmission.getUser().getId() : null)
                .userUsername(quizSubmission.getUser() != null ? quizSubmission.getUser().getUsername() : null)
                .startedAt(quizSubmission.getStartedAt())
                .submittedAt(quizSubmission.getSubmittedAt())
                .score(quizSubmission.getScore())
                .answers(answersDto)
                .createdAt(quizSubmission.getCreatedAt())
                .updatedAt(quizSubmission.getUpdatedAt())
                .status(quizSubmission.getStatus())
                .isDeleted(quizSubmission.getIsDeleted())
                .build();
    }

    public static Page<QuizSubmissionResponseDto> convertPage(Page<QuizSubmission> quizSubmissionPage) {
        List<QuizSubmissionResponseDto> quizSubmissionResponseDTOs = quizSubmissionPage.getContent()
                .stream()
                .map(QuizSubmissionResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                quizSubmissionResponseDTOs,
                quizSubmissionPage.getPageable(),
                quizSubmissionPage.getTotalElements()
        );
    }
}
