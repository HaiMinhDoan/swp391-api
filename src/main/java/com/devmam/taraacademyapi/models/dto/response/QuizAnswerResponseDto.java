package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.QuizAnswer;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.QuizAnswer}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class QuizAnswerResponseDto implements Serializable {
    private final Integer id;
    private final Integer quizId;
    private final String quizQuestion;
    private final UUID userId;
    private final String userUsername;
    private final String answer;
    private final Boolean isCorrect;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static QuizAnswerResponseDto toDTO(QuizAnswer quizAnswer) {
        return QuizAnswerResponseDto.builder()
                .id(quizAnswer.getId())
//                .quizId(quizAnswer.getQuiz() != null ? quizAnswer.getQuiz().getId() : null)
//                .quizQuestion(quizAnswer.getQuiz() != null ? quizAnswer.getQuiz().getQuestion() : null)
//                .userId(quizAnswer.getUser() != null ? quizAnswer.getUser().getId() : null)
//                .userUsername(quizAnswer.getUser() != null ? quizAnswer.getUser().getUsername() : null)
//                .answer(quizAnswer.getAnswer())
                .isCorrect(quizAnswer.getIsCorrect())
                .createdAt(quizAnswer.getCreatedAt())
                .updatedAt(quizAnswer.getUpdatedAt())
                .status(quizAnswer.getStatus())
                .isDeleted(quizAnswer.getIsDeleted())
                .build();
    }

    public static Page<QuizAnswerResponseDto> convertPage(Page<QuizAnswer> quizAnswerPage) {
        List<QuizAnswerResponseDto> quizAnswerResponseDTOs = quizAnswerPage.getContent()
                .stream()
                .map(QuizAnswerResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                quizAnswerResponseDTOs,
                quizAnswerPage.getPageable(),
                quizAnswerPage.getTotalElements()
        );
    }
}
