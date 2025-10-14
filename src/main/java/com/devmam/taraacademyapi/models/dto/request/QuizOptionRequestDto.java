package com.devmam.taraacademyapi.models.dto.request;

import lombok.*;

/**
 * DTO for QuizOption creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuizOptionRequestDto {
    
    private Integer quizId;
    
    private String optionText;
    
    private Boolean isCorrect;
    
    private Integer orderIndex;
    
    private Integer status;
}
