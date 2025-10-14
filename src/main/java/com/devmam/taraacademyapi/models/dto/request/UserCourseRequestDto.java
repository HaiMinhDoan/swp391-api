package com.devmam.taraacademyapi.models.dto.request;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for UserCourse creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserCourseRequestDto {
    
    private UUID userId;
    
    private Integer courseId;
    
    private Instant enrolledAt;
    
    private Instant completedAt;
    
    private Integer progress;
    
    private Integer status;
}
