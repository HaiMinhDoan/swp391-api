package com.devmam.taraacademyapi.models.dto.request;

import lombok.*;

import java.math.BigDecimal;
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
    
    private Integer lessonId;
    
    private UUID userId;
    
    private Instant startedAt;
    
    private Instant submittedAt;
    
    private BigDecimal score;
    
    private Integer status;
}
