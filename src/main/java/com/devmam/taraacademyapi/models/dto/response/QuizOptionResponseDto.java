package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.QuizOption;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.QuizOption}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class QuizOptionResponseDto implements Serializable {
    private final Integer id;
    private final Integer quizId;
    private final String optionText;
    private final Boolean isCorrect;
    private final Integer orderIndex;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static QuizOptionResponseDto toDTO(QuizOption quizOption) {
        return QuizOptionResponseDto.builder()
                .id(quizOption.getId())
                .quizId(quizOption.getQuiz() != null ? quizOption.getQuiz().getId() : null)
                .isCorrect(quizOption.getIsCorrect())
                .optionText(quizOption.getContent())
//                .orderIndex(quizOption.getOrderIndex())
                .createdAt(quizOption.getCreatedAt())
                .updatedAt(quizOption.getUpdatedAt())
                .status(quizOption.getStatus())
                .isDeleted(quizOption.getIsDeleted())
                .build();
    }

    public static Page<QuizOptionResponseDto> convertPage(Page<QuizOption> quizOptionPage) {
        List<QuizOptionResponseDto> quizOptionResponseDTOs = quizOptionPage.getContent()
                .stream()
                .map(QuizOptionResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                quizOptionResponseDTOs,
                quizOptionPage.getPageable(),
                quizOptionPage.getTotalElements()
        );
    }
}
