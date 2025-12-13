package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.QuizAnswer;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
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
    private final Integer submissionId;
    private final QuizSubmissionResponseDto submission;
    private final Integer questionId;
    private final QuizResponseDto question;
    private final Integer selectedOptionId;
    private final String answerText;
    private final String teacherNote;
    private final Integer score;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;
    private Boolean isCorrect;

    public static QuizAnswerResponseDto toDTO(QuizAnswer quizAnswer) {
        return toDTO(quizAnswer, true);
    }

    public static QuizAnswerResponseDto toDTO(QuizAnswer quizAnswer, boolean includeSubmission) {
        QuizResponseDto questionDto = null;
        if (quizAnswer.getQuestion() != null) {
            questionDto = QuizResponseDto.toDTO(quizAnswer.getQuestion());
        }
        
        QuizSubmissionResponseDto submissionDto = null;
        if (includeSubmission && quizAnswer.getSubmission() != null) {
            submissionDto = QuizSubmissionResponseDto.toDTO(quizAnswer.getSubmission());
        }
        
        return QuizAnswerResponseDto.builder()
                .id(quizAnswer.getId())
                .submissionId(quizAnswer.getSubmission() != null ? quizAnswer.getSubmission().getId() : null)
                .submission(submissionDto)
                .questionId(quizAnswer.getQuestion() != null ? quizAnswer.getQuestion().getId() : null)
                .question(questionDto)
                .selectedOptionId(quizAnswer.getSelectedOptionId())
                .answerText(quizAnswer.getAnswerText())
                .teacherNote(quizAnswer.getTeacherNote())
                .score(quizAnswer.getScore())
                .createdAt(quizAnswer.getCreatedAt())
                .updatedAt(quizAnswer.getUpdatedAt())
                .status(quizAnswer.getStatus())
                .isDeleted(quizAnswer.getIsDeleted())
                .isCorrect(quizAnswer.getIsCorrect())
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
