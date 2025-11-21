package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Quiz;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Quiz}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class QuizResponseDto implements Serializable {
    private final Integer id;
    private final Integer lessonId;
    private final String lessonTitle;
    private final String type;
    private final String question;
    private final String explanation;
    private final Integer timeLimit;
    private final List<QuizOptionResponseDto> options;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static QuizResponseDto toDTO(Quiz quiz) {
        List<QuizOptionResponseDto> optionsDto = null;
        if (quiz.getOptions() != null) {
            optionsDto = quiz.getOptions().stream()
                    .map(QuizOptionResponseDto::toDTO)
                    .collect(Collectors.toList());
        }
        
        return QuizResponseDto.builder()
                .id(quiz.getId())
                .lessonId(quiz.getLesson() != null ? quiz.getLesson().getId() : null)
                .lessonTitle(quiz.getLesson() != null ? quiz.getLesson().getTitle() : null)
                .type(quiz.getType())
                .question(quiz.getQuestion())
//                .explanation(quiz.getExplanation())
//                .timeLimit(quiz.getTimeLimit())
                .options(optionsDto)
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .status(quiz.getStatus())
                .isDeleted(quiz.getIsDeleted())
                .build();
    }

    public static Page<QuizResponseDto> convertPage(Page<Quiz> quizPage) {
        List<QuizResponseDto> quizResponseDTOs = quizPage.getContent()
                .stream()
                .map(QuizResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                quizResponseDTOs,
                quizPage.getPageable(),
                quizPage.getTotalElements()
        );
    }
}
