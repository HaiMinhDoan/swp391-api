package com.devmam.taraacademyapi.models.dto.request;

import lombok.*;

/**
 * DTO for QuizAnswer creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuizAnswerRequestDto {

    private Integer submissionId;

    private Integer questionId;

    private Integer selectedOptionId;

    private String answerText;

    private Boolean isCorrect;

    private String teacherNote;

    private Integer score;

    private Integer status;
}
