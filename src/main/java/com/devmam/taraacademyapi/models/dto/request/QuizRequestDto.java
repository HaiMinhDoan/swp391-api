package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for Quiz creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuizRequestDto {
    
    private Integer lessonId;
    
    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;
    
    @Size(max = 255, message = "Question must not exceed 255 characters")
    private String question;
    
    private String explanation;
    
    private Integer timeLimit;
    
    private Integer status;
    
    private String fileUrl;
    
    private String teacherNote;
}
