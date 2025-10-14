package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for Lesson creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LessonRequestDto {
    
    private Integer stageId;
    
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    private String content;
    
    @Size(max = 255, message = "Video URL must not exceed 255 characters")
    private String videoUrl;
    
    private Integer orderIndex;
    
    private Integer status;
}
