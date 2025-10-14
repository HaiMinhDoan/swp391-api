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
    
    private Integer quizId;
    
    private Integer userId;
    
    private String answer;
    
    private Boolean isCorrect;
    
    private Integer status;
}
