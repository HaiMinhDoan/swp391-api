package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.QuizSubmission;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
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
    private final Integer quizId;
    private final String quizQuestion;
    private final UUID userId;
    private final String userUsername;
    private final String answers;
    private final Integer score;
    private final Instant submittedAt;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static QuizSubmissionResponseDto toDTO(QuizSubmission quizSubmission) {
        return QuizSubmissionResponseDto.builder()
                .id(quizSubmission.getId())
                .quizId(quizSubmission.getQuiz() != null ? quizSubmission.getQuiz().getId() : null)
                .quizQuestion(quizSubmission.getQuiz() != null ? quizSubmission.getQuiz().getQuestion() : null)
                .userId(quizSubmission.getUser() != null ? quizSubmission.getUser().getId() : null)
                .userUsername(quizSubmission.getUser() != null ? quizSubmission.getUser().getUsername() : null)
//                .answers(quizSubmission.getAnswers())
//                .score(quizSubmission.getScore())
                .submittedAt(quizSubmission.getSubmittedAt())
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
