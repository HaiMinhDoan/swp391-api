package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * DTO for StageLesson with list of Lessons
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StageLessonWithLessonsResponseDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private Integer orderIndex;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer status;
    private Integer isDeleted;
    private List<LessonResponseDto> lessons;
}

