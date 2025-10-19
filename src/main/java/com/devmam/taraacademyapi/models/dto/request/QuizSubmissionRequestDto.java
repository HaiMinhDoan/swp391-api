package com.devmam.taraacademyapi.models.dto.request;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for QuizSubmission creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuizSubmissionRequestDto {
    
    private Integer quizId;
    
    private UUID userId;
    
    private String answers;
    
    private Integer score;
    
    private Instant submittedAt;
    
    private Integer status;
}
